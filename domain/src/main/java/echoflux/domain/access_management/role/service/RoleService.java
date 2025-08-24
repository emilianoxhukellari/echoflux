package echoflux.domain.access_management.role.service;

import echoflux.domain.jooq.tables.pojos.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface RoleService {

    Role getById(@NotNull Long id);

    Long save(@Valid @NotNull SaveRoleCommand command);

}
