package echoflux.domain.access_management.endpoint;

import echoflux.domain.access_management.application_user.service.ChangePasswordCommand;
import echoflux.domain.access_management.application_user.service.CreateApplicationUserCommand;
import echoflux.domain.access_management.application_user.service.UpdateApplicationUserCommand;
import echoflux.domain.access_management.role.service.SaveRoleCommand;
import echoflux.domain.core.security.PermissionType;
import echoflux.domain.core.security.RequiredPermissions;
import echoflux.domain.jooq.tables.pojos.ApplicationUser;
import echoflux.domain.jooq.tables.pojos.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface AccessManagementEndpoint {

    ApplicationUser getApplicationUserById(@NotNull Long id);

    Role getRoleById(@NotNull Long id);

    @RequiredPermissions({PermissionType.ACCESS_MANAGEMENT_ROLE_CREATE, PermissionType.ACCESS_MANAGEMENT_ROLE_UPDATE})
    Long saveRole(@Valid @NotNull SaveRoleCommand command);

    @RequiredPermissions(PermissionType.ACCESS_MANAGEMENT_APPLICATION_USER_CREATE)
    Long createApplicationUser(@Valid @NotNull CreateApplicationUserCommand command);

    @RequiredPermissions(PermissionType.ACCESS_MANAGEMENT_APPLICATION_USER_UPDATE)
    Long updateApplicationUser(@Valid @NotNull UpdateApplicationUserCommand command);

    @RequiredPermissions(PermissionType.ACCESS_MANAGEMENT_APPLICATION_USER_UPDATE)
    Long changeApplicationUserPassword(@Valid @NotNull ChangePasswordCommand command);

}
