package transcribe.domain.template.data;

import transcribe.domain.core.repository.EnhancedJpaRepository;

import java.util.Optional;

public interface TemplateRepository extends EnhancedJpaRepository<TemplateEntity, Long> {

    Optional<TemplateEntity> findByName(String name);

    @Override
    default Class<TemplateEntity> getBeanType() {
        return TemplateEntity.class;
    }

}