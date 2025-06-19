package echoflux.domain.application_user.service;

import echoflux.domain.application_user.data.ApplicationUserEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import echoflux.domain.core.password.PasswordMatch;

@Validated
public interface ApplicationUserService {

    ApplicationUserEntity create(@Valid @NotNull @PasswordMatch CreateApplicationUserCommand command);

    ApplicationUserEntity update(@Valid @NotNull UpdateApplicationUserCommand command);

    ApplicationUserEntity changePassword(@Valid @NotNull @PasswordMatch ChangePasswordCommand command);

    void deleteById(@NotNull Long id);

}
