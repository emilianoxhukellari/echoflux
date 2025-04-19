package echoflux.domain.operation.data;

import echoflux.domain.core.repository.EnhancedJpaRepository;

public interface OperationRepository extends EnhancedJpaRepository<OperationEntity, Long> {

    @Override
    default Class<OperationEntity> getBeanType() {
        return OperationEntity.class;
    }

}
