package transcribe.domain.application_user.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.core.password.PasswordMatch;

@Validated
public interface ApplicationUserService {

    ApplicationUserEntity create(@Valid @NotNull @PasswordMatch CreateApplicationUserCommand command);

    ApplicationUserEntity update(@Valid @NotNull UpdateApplicationUserCommand command);

    ApplicationUserEntity changePassword(@Valid @NotNull @PasswordMatch ChangePasswordCommand command);

    void delete(@NotNull Long id);

}
