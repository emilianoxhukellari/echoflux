package transcribe.domain.operation.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OperationRepository extends JpaRepository<OperationEntity, Long>, JpaSpecificationExecutor<OperationEntity> {
}
