package echoflux.domain.core.repository;

import echoflux.domain.core.data.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@NoRepositoryBean
@Transactional(readOnly = true)
@Validated
public interface CoreJpaRepository<E extends BaseEntity<ID>, ID> extends ProjectionJpaRepository<E, ID>, JpaRepository<E, ID> {
}
