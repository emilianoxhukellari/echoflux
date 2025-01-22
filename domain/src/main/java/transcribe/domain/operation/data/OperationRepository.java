package transcribe.domain.operation.data;

import transcribe.domain.core.repository.EnhancedJpaRepository;

public interface OperationRepository extends EnhancedJpaRepository<OperationEntity, Long> {

    @Override
    default Class<OperationEntity> getBeanType() {
        return OperationEntity.class;
    }

}
