package echoflux.domain.transcription_word.data;

import echoflux.domain.core.repository.CoreJpaRepository;

import java.util.List;

public interface TranscriptionWordRepository extends CoreJpaRepository<TranscriptionWordEntity, Long> {

    <T> List<T> findAllByTranscriptionIdOrderBySequence(Long transcriptionId, Class<T> type);

}