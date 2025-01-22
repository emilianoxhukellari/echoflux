package transcribe.domain.application_user.data;

import transcribe.domain.core.repository.EnhancedJpaRepository;

import java.util.Optional;

public interface ApplicationUserRepository extends EnhancedJpaRepository<ApplicationUserEntity, Long> {

    Optional<ApplicationUserEntity> findByUsername(String username);

    @Override
    default Class<ApplicationUserEntity> getBeanType() {
        return ApplicationUserEntity.class;
    }

}
