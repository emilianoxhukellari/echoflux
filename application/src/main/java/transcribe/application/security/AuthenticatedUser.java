package transcribe.application.security;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.application_user.data.Role;

import java.util.Optional;

@Validated
public interface AuthenticatedUser {

    Optional<ApplicationUserEntity> find();

    void logout();

    boolean hasRole(@NotNull Role role);

    boolean isAdmin();

}
