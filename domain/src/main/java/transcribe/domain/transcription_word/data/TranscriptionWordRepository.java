package transcribe.domain.transcription_word.data;
import transcribe.domain.core.repository.EnhancedJpaRepository;

public interface TranscriptionWordRepository extends EnhancedJpaRepository<TranscriptionWordEntity, Long> {

    @Override
    default Class<TranscriptionWordEntity> getBeanType() {
        return TranscriptionWordEntity.class;
    }

}