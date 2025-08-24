package echoflux.domain.access_management.application_user.service;

import echoflux.domain.jooq.tables.pojos.ApplicationUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface ApplicationUserService {

    ApplicationUser getById(@NotNull Long id);

    Long create(@Valid @NotNull CreateApplicationUserCommand command);

    Long update(@Valid @NotNull UpdateApplicationUserCommand command);

    Long changePassword(@Valid @NotNull ChangePasswordCommand command);

}
