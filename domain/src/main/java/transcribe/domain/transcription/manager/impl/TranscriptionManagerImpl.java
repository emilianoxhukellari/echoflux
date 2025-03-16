package transcribe.domain.transcription.manager.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import transcribe.core.core.validate.guard.Guard;
import transcribe.core.word.common.Word;
import transcribe.core.word.processor.SpeakerSegmentAssembler;
import transcribe.core.word.processor.WordPatcher;
import transcribe.domain.transcription.manager.ReplaceWordsCommand;
import transcribe.domain.transcription.manager.TranscriptionManager;
import transcribe.domain.transcription.service.TranscriptionService;
import transcribe.domain.transcription_word.data.SpeakerSegmentDto;
import transcribe.domain.transcription_word.data.WordDto;
import transcribe.domain.transcription_word.mapper.TranscriptionWordMapper;
import transcribe.domain.transcription_word.service.TranscriptionWordService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TranscriptionManagerImpl implements TranscriptionManager {

    private final TranscriptionService transcriptionService;
    private final TranscriptionWordService transcriptionWordService;
    private final TranscriptionWordMapper transcriptionWordMapper;


    @Override
    public List<WordDto> getTranscriptionSpeakerWords(Long transcriptionId) {
        return transcriptionWordService.findAllByTranscriptionId(transcriptionId);
    }

    @Override
    public List<SpeakerSegmentDto> getTranscriptionSpeakerSegments(Long transcriptionId) {
        var words = getTranscriptionSpeakerWords(transcriptionId);

        return SpeakerSegmentAssembler.assembleAll(words, SpeakerSegmentDto::new);
    }

    @Transactional
    @Override
    public void createWords(Long transcriptionId, List<Word> words) {
        var transcriptionEntity = transcriptionService.getByIdFetchWords(transcriptionId);
        Guard.empty(transcriptionEntity.getWords(), "words must be empty");

        var wordEntities = transcriptionWordMapper.toEntities(words);
        transcriptionEntity.addWords(wordEntities);
    }

    @Transactional
    @Override
    public void replaceAllWords(Long transcriptionId, String words) {
        var transcriptionEntity = transcriptionService.getByIdFetchWords(transcriptionId);
        var wordSize = transcriptionEntity.getWords().size();

        replaceWords(
                ReplaceWordsCommand.builder()
                        .transcriptionId(transcriptionId)
                        .fromSequence(0)
                        .size(wordSize)
                        .words(words)
                        .build()
        );
    }

    @Transactional
    @Override
    public void replaceWords(ReplaceWordsCommand command) {
        var transcriptionEntity = transcriptionService.getByIdFetchWords(command.getTranscriptionId());
        var wordEntities = transcriptionEntity.getWords();
        Guard.le(command.getFromSequence() + command.getSize(), wordEntities.size());

        int toIndexTarget = command.getFromSequence() + command.getSize();
        var targetWordEntities = wordEntities.subList(command.getFromSequence(), toIndexTarget);

        var words = transcriptionWordMapper.toWords(targetWordEntities);
        var patchedWords = WordPatcher.patchAll(words, command.getWords(), Word::new);

        int toIndexPatch = command.getFromSequence() + patchedWords.size();
        int fromIndex = command.getFromSequence();

        while (fromIndex < toIndexPatch && fromIndex < toIndexTarget) {
            var word = patchedWords.get(fromIndex - command.getFromSequence());
            var wordEntity = targetWordEntities.get(fromIndex - command.getFromSequence());

            wordEntity.setContent(word.getContent());
            wordEntity.setSpeakerName(word.getSpeakerName());
            wordEntity.setStartOffsetMillis(word.getStartOffsetMillis());
            wordEntity.setEndOffsetMillis(word.getEndOffsetMillis());

            fromIndex++;
        }

        if (fromIndex < toIndexTarget) {
            transcriptionEntity.removeWords(fromIndex, toIndexTarget);
        } else if (fromIndex < toIndexPatch) {
            var entitiesToAdd = transcriptionWordMapper.toEntities(patchedWords.subList(fromIndex, toIndexPatch));
            transcriptionEntity.addWords(fromIndex, entitiesToAdd);
        }
    }

}