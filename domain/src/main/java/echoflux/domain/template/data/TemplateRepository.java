package echoflux.domain.template.data;

import echoflux.domain.core.repository.CoreJpaRepository;

public interface TemplateRepository extends CoreJpaRepository<TemplateEntity, Long> {

    TemplateProjection getProjectionByName(String name);

}