package echoflux.domain.application_user.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import echoflux.domain.application_user.data.ApplicationUserProjection;
import echoflux.domain.core.password.PasswordMatch;

@Validated
public interface ApplicationUserService {

    ApplicationUserProjection create(@Valid @NotNull @PasswordMatch CreateApplicationUserCommand command);

    ApplicationUserProjection patch(@Valid @NotNull UpdateApplicationUserCommand command);

    ApplicationUserProjection changePassword(@Valid @NotNull @PasswordMatch ChangePasswordCommand command);

    void deleteById(@NotNull Long id);

}
