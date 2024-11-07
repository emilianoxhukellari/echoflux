package transcribe.domain.template.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TemplateRepository extends JpaRepository<TemplateEntity, Long>,
        JpaSpecificationExecutor<TemplateEntity> {

    Optional<TemplateEntity> findByName(String name);

}