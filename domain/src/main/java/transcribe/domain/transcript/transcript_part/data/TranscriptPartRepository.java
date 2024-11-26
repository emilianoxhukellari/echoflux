package transcribe.domain.transcript.transcript_part.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TranscriptPartRepository extends JpaRepository<TranscriptPartEntity, Long>,
        JpaSpecificationExecutor<TranscriptPartEntity> {

    List<TranscriptPartEntity> findAllByTranscriptionIdOrderBySequence(Long transcriptionId);

}
