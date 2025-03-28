package transcribe.domain.transcription.manager.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import transcribe.core.document.Paragraph;
import transcribe.core.word.common.WordInfo;
import transcribe.core.word.processor.SpeakerSegmentAssembler;
import transcribe.core.word.processor.TranscriptRenderer;
import transcribe.domain.transcription.manager.TranscriptionManager;
import transcribe.domain.transcription.service.TranscriptionService;
import transcribe.domain.transcription_word.data.SpeakerSegmentDto;
import transcribe.domain.transcription_word.data.TranscriptionWordEntity;
import transcribe.domain.transcription_word.data.WordDto;
import transcribe.domain.transcription_word.service.TranscriptionWordService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TranscriptionManagerImpl implements TranscriptionManager {

    private final EntityManager entityManager;
    private final TranscriptionService transcriptionService;
    private final TranscriptionWordService transcriptionWordService;

    @Override
    public List<Paragraph> renderTranscript(Long transcriptionId, boolean withTimestamps) {
        var speakerSegments = getTranscriptionSpeakerSegments(transcriptionId);

        return TranscriptRenderer.render(speakerSegments, withTimestamps);
    }

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
    public <T extends WordInfo> void saveWords(Long transcriptionId, List<T> words) {
        var transcriptionEntity = transcriptionService.getByIdFetchWords(transcriptionId);

        int wordEntityIndex = 0;
        int wordInfoIndex = 0;

        while (wordEntityIndex < transcriptionEntity.getWords().size() && wordInfoIndex < words.size()) {
            var wordEntity = transcriptionEntity.getWords().get(wordEntityIndex);
            var wordInfo = words.get(wordInfoIndex);

            wordEntity.setContent(wordInfo.getContent());
            wordEntity.setSpeakerName(wordInfo.getSpeakerName());
            wordEntity.setStartOffsetMillis(wordInfo.getStartOffsetMillis());
            wordEntity.setEndOffsetMillis(wordInfo.getEndOffsetMillis());

            wordEntityIndex++;
            wordInfoIndex++;
        }

        if (wordEntityIndex < transcriptionEntity.getWords().size()) {
            transcriptionEntity.removeWords(wordEntityIndex, transcriptionEntity.getWords().size());
        } else if (wordInfoIndex < words.size()) {
            var entitiesToAdd = words.subList(wordInfoIndex, words.size())
                    .stream()
                    .map(w ->
                            TranscriptionWordEntity.builder()
                                    .content(w.getContent())
                                    .speakerName(w.getSpeakerName())
                                    .startOffsetMillis(w.getStartOffsetMillis())
                                    .endOffsetMillis(w.getEndOffsetMillis())
                                    .build()
                    )
                    .toList();

            transcriptionEntity.addWords(entitiesToAdd);
        }

        entityManager.merge(transcriptionEntity);
    }

}