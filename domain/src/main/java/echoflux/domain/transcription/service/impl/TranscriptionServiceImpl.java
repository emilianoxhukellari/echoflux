package echoflux.domain.transcription.service.impl;

import echoflux.core.document.Paragraph;
import echoflux.core.word.common.WordInfo;
import echoflux.core.word.processor.SpeakerSegmentAssembler;
import echoflux.core.word.processor.TranscriptRenderer;
import echoflux.domain.transcription.data.ScalarTranscriptionProjection;
import echoflux.domain.transcription_word.data.SequencedWord;
import echoflux.domain.transcription_word.data.SpeakerSegment;
import echoflux.domain.transcription_word.data.TranscriptionWordEntity;
import echoflux.domain.transcription_word.data.TranscriptionWordRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import echoflux.domain.application_user.data.ApplicationUserRepository;
import echoflux.domain.transcription.data.TranscriptionEntity;
import echoflux.domain.transcription.data.TranscriptionRepository;
import echoflux.domain.transcription.mapper.TranscriptionMapper;
import echoflux.domain.transcription.service.CreateTranscriptionCommand;
import echoflux.domain.transcription.service.PatchTranscriptionCommand;
import echoflux.domain.transcription.service.RenameTranscriptionCommand;
import echoflux.domain.transcription.service.TranscriptionService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TranscriptionServiceImpl implements TranscriptionService {

    private final ApplicationUserRepository applicationUserRepository;
    private final TranscriptionRepository transcriptionRepository;
    private final TranscriptionWordRepository transcriptionWordRepository;
    private final TranscriptionMapper transcriptionMapper;
    private final EntityManager entityManager;

    @Override
    public TranscriptionEntity getById(Long id) {
        return transcriptionRepository.getReferenceById(id);
    }

    @Override
    public TranscriptionEntity getWithWordsById(Long id) {
        return transcriptionRepository.findWithWordsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transcription with id [%d] not found".formatted(id)));
    }

    @Override
    public ScalarTranscriptionProjection getScalarProjectedById(Long id) {
        return transcriptionRepository.findById(id, ScalarTranscriptionProjection.class)
                .orElseThrow(() -> new EntityNotFoundException("Transcription with id [%d] not found".formatted(id)));
    }

    @Override
    @Transactional
    public ScalarTranscriptionProjection create(CreateTranscriptionCommand command) {
        var applicationUser = applicationUserRepository.getReferenceById(command.getApplicationUserId());
        var transcription = transcriptionMapper.toEntity(command);
        transcription.setApplicationUser(applicationUser);

        var saved = transcriptionRepository.save(transcription);

        return transcriptionMapper.toProjection(saved);
    }

    @Override
    @Transactional
    public ScalarTranscriptionProjection patch(PatchTranscriptionCommand command) {
        var transcription = transcriptionRepository.getReferenceById(command.getId());
        var patchedTranscription = transcriptionMapper.patch(transcription, command);

        var saved = transcriptionRepository.save(patchedTranscription);

        return transcriptionMapper.toProjection(saved);
    }

    @Override
    @Transactional
    public ScalarTranscriptionProjection rename(RenameTranscriptionCommand command) {
        return patch(
                PatchTranscriptionCommand.builder()
                        .id(command.getId())
                        .name(command.getName())
                        .build()
        );
    }

    @Override
    public List<SequencedWord> getTranscriptionWords(Long transcriptionId) {
        return transcriptionWordRepository.findAllByTranscriptionIdOrderBySequence(transcriptionId, SequencedWord.class);
    }

    @Override
    public List<SpeakerSegment> getTranscriptionSpeakerSegments(Long transcriptionId) {
        var words = getTranscriptionWords(transcriptionId);

        return SpeakerSegmentAssembler.assembleAll(words, SpeakerSegment::new);
    }

    @Override
    public List<Paragraph> renderTranscript(Long transcriptionId, boolean withTimestamps) {
        var speakerSegments = getTranscriptionSpeakerSegments(transcriptionId);

        return TranscriptRenderer.render(speakerSegments, withTimestamps);
    }

    @Transactional
    @Override
    public <T extends WordInfo> void saveWords(Long transcriptionId, List<T> words) {
        var transcriptionEntity = getWithWordsById(transcriptionId);

        int wordEntityIndex = 0;
        int wordInfoIndex = 0;
        var transcriptionWords = transcriptionEntity.getWords();

        while (wordEntityIndex < transcriptionWords.size() && wordInfoIndex < words.size()) {
            var wordEntity = transcriptionWords.get(wordEntityIndex);
            var wordInfo = words.get(wordInfoIndex);

            wordEntity.setContent(wordInfo.getContent());
            wordEntity.setSpeakerName(wordInfo.getSpeakerName());
            wordEntity.setStartOffsetMillis(wordInfo.getStartOffsetMillis());
            wordEntity.setEndOffsetMillis(wordInfo.getEndOffsetMillis());

            wordEntityIndex++;
            wordInfoIndex++;
        }

        if (wordEntityIndex < transcriptionWords.size()) {
            transcriptionEntity.removeWords(wordEntityIndex, transcriptionWords.size());
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
