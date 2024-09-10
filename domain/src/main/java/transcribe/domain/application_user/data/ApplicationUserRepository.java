package transcribe.domain.application_user.data;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ApplicationUserRepository
        extends JpaRepository<ApplicationUserEntity, Long>, JpaSpecificationExecutor<ApplicationUserEntity> {

    Optional<ApplicationUserEntity> findByUsername(String username);

}
