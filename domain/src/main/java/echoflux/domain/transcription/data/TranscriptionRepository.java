package echoflux.domain.transcription.data;

import org.springframework.data.jpa.repository.Query;
import echoflux.domain.core.repository.EnhancedJpaRepository;

import java.util.Optional;

public interface TranscriptionRepository extends EnhancedJpaRepository<TranscriptionEntity, Long> {

    @Query("SELECT t FROM TranscriptionEntity t LEFT JOIN FETCH t.words w WHERE t.id = :id")
    Optional<TranscriptionEntity> findByIdFetchWords(Long id);

    <T> Optional<T> findById(Long id, Class<T> projection);

    @Override
    default Class<TranscriptionEntity> getBeanType() {
        return TranscriptionEntity.class;
    }

}