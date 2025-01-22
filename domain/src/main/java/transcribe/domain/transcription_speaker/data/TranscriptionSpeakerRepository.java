package transcribe.domain.transcription_speaker.data;

import transcribe.domain.core.repository.EnhancedJpaRepository;


public interface TranscriptionSpeakerRepository extends EnhancedJpaRepository<TranscriptionSpeakerEntity, Long> {

    @Override
    default Class<TranscriptionSpeakerEntity> getBeanType() {
        return TranscriptionSpeakerEntity.class;
    }

}
