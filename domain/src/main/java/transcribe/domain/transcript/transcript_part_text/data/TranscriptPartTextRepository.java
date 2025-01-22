package transcribe.domain.transcript.transcript_part_text.data;
import transcribe.domain.core.repository.EnhancedJpaRepository;

import java.util.List;
import java.util.Optional;

public interface TranscriptPartTextRepository extends EnhancedJpaRepository<TranscriptPartTextEntity, Long> {

    Optional<TranscriptPartTextEntity> findFirstByTranscriptPartIdOrderByVersionDesc(Long transcriptPartId);

    List<TranscriptPartTextEntity> findAllByTranscriptPartId(Long transcriptPartId);

    @Override
    default Class<TranscriptPartTextEntity> getBeanType() {
        return TranscriptPartTextEntity.class;
    }

}
