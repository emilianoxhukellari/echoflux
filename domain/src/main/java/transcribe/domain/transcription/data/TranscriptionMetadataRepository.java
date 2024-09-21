package transcribe.domain.transcription.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TranscriptionMetadataRepository extends JpaRepository<TranscriptionMetadataEntity, Long>,
        JpaSpecificationExecutor<TranscriptionMetadataEntity> {

    @Query(value = """
             SELECT AVG(recent_transcription.transcribe_duration_millis * 1.0 / recent_transcription.length_millis)\s
             FROM (
                 SELECT t.transcribe_duration_millis, t.length_millis\s
                 FROM transcription_metadata t\s
                 WHERE t.length_millis IS NOT NULL\s
                   AND t.process_duration_millis IS NOT NULL\s
                   AND t.length_millis > 0\s
                 ORDER BY t.id DESC\s
                 LIMIT :limit
             ) recent_transcription
            \s""", nativeQuery = true)
    Optional<Double> findAverageRealTimeFactor(@Param("limit") int limit);

}