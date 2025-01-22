package transcribe.domain.completion.data;

import transcribe.domain.core.repository.EnhancedJpaRepository;

public interface CompletionRepository extends EnhancedJpaRepository<CompletionEntity, Long> {

    @Override
    default Class<CompletionEntity> getBeanType() {
        return CompletionEntity.class;
    }

}
