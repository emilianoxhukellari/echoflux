package transcribe.domain.application_user.data;

import org.springframework.data.jpa.repository.EntityGraph;
import transcribe.domain.core.repository.EnhancedJpaRepository;

import java.util.Optional;

public interface ApplicationUserRepository extends EnhancedJpaRepository<ApplicationUserEntity, Long> {

    @EntityGraph(attributePaths = {"roles"})
    Optional<ApplicationUser> findByUsername(String username);

    @Override
    default Class<ApplicationUserEntity> getBeanType() {
        return ApplicationUserEntity.class;
    }

}
