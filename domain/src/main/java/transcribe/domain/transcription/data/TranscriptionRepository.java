package transcribe.domain.transcription.data;

import transcribe.domain.core.repository.EnhancedJpaRepository;

public interface TranscriptionRepository extends EnhancedJpaRepository<TranscriptionEntity, Long> {

    @Override
    default Class<TranscriptionEntity> getBeanType() {
        return TranscriptionEntity.class;
    }

}