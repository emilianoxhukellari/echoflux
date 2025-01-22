package transcribe.domain.transcript.transcript_part.data;

import transcribe.domain.core.repository.EnhancedJpaRepository;

import java.util.List;
import java.util.Optional;

public interface TranscriptPartRepository extends EnhancedJpaRepository<TranscriptPartEntity, Long> {

    List<TranscriptPartEntity> findAllByTranscriptionIdOrderBySequence(Long transcriptionId);

    Optional<TranscriptPartEntity> findByTranscriptionIdAndSequence(Long transcriptionId, Integer sequence);

    @Override
    default Class<TranscriptPartEntity> getBeanType() {
        return TranscriptPartEntity.class;
    }

}
