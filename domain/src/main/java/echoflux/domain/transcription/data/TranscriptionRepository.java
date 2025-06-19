package echoflux.domain.transcription.data;

import echoflux.domain.core.repository.CoreJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface TranscriptionRepository extends CoreJpaRepository<TranscriptionEntity, Long> {

    @EntityGraph(attributePaths = {TranscriptionEntity_.WORDS})
    Optional<TranscriptionEntity> findWithWordsById(Long id);

    <T> Optional<T> findById(Long id, Class<T> projection);

}