package transcribe.domain.completion.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CompletionRepository extends JpaRepository<CompletionEntity, Long>,
        JpaSpecificationExecutor<CompletionEntity> {
}
