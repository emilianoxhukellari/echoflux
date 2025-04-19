package echoflux.application.security;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import echoflux.domain.application_user.data.ApplicationUser;
import echoflux.domain.application_user.data.Role;

import java.util.Optional;

@Validated
public interface AuthenticatedUser {

    default ApplicationUser get() {
        return find().orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    default Long getId() {
        return get().getId();
    }

    Optional<ApplicationUser> find();

    void logout();

    boolean hasRole(@NotNull Role role);

    boolean isAdmin();

}
