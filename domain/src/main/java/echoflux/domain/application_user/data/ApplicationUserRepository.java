package echoflux.domain.application_user.data;

import echoflux.domain.core.repository.CoreJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface ApplicationUserRepository extends CoreJpaRepository<ApplicationUserEntity, Long> {

    @EntityGraph(attributePaths = {ApplicationUserEntity_.ROLES})
    Optional<ApplicationUserProjection> findProjectedByUsername(String username);

}
