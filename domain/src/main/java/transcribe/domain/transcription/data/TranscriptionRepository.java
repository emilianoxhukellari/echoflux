package transcribe.domain.transcription.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TranscriptionRepository
        extends JpaRepository<TranscriptionEntity, Long>, JpaSpecificationExecutor<TranscriptionEntity> {
}
