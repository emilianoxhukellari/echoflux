package transcribe.domain.transcription_word.data;

import transcribe.domain.core.repository.EnhancedJpaRepository;

import java.util.List;

public interface TranscriptionWordRepository extends EnhancedJpaRepository<TranscriptionWordEntity, Long> {

    List<WordDto> findAllByTranscriptionId(Long transcriptionId);

    @Override
    default Class<TranscriptionWordEntity> getBeanType() {
        return TranscriptionWordEntity.class;
    }

}