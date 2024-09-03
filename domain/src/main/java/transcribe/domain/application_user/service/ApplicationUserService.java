package transcribe.domain.application_user.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.application_user.data.ApplicationUserEntity;

@Validated
public interface ApplicationUserService {

    ApplicationUserEntity create(@Valid @NotNull CreateApplicationUserCommand command);

}
