package transcribe.domain.transcript.transcript_part_text.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TranscriptPartTextRepository extends JpaRepository<TranscriptPartTextEntity, Long>,
        JpaSpecificationExecutor<TranscriptPartTextEntity> {

    Optional<TranscriptPartTextEntity> findFirstByTranscriptPartIdOrderByVersionDesc(Long transcriptPartId);

}
