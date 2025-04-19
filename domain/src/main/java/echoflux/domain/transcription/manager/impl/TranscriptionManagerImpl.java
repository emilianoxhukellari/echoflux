package echoflux.domain.transcription.manager.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import echoflux.core.document.Paragraph;
import echoflux.core.word.common.WordInfo;
import echoflux.core.word.processor.SpeakerSegmentAssembler;
import echoflux.core.word.processor.TranscriptRenderer;
import echoflux.domain.transcription.manager.TranscriptionManager;
import echoflux.domain.transcription.service.TranscriptionService;
import echoflux.domain.transcription_word.data.SpeakerSegmentDto;
import echoflux.domain.transcription_word.data.TranscriptionWordEntity;
import echoflux.domain.transcription_word.data.WordDto;
import echoflux.domain.transcription_word.service.TranscriptionWordService;

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