package echoflux.domain.access_management.endpoint.impl;

import echoflux.domain.access_management.endpoint.AccessManagementEndpoint;
import echoflux.domain.access_management.application_user.service.ApplicationUserService;
import echoflux.domain.access_management.application_user.service.ChangePasswordCommand;
import echoflux.domain.access_management.application_user.service.CreateApplicationUserCommand;
import echoflux.domain.access_management.application_user.service.UpdateApplicationUserCommand;
import echoflux.domain.access_management.role.service.RoleService;
import echoflux.domain.access_management.role.service.SaveRoleCommand;
import echoflux.domain.core.security.Endpoint;
import echoflux.domain.jooq.tables.pojos.ApplicationUser;
import echoflux.domain.jooq.tables.pojos.Role;
import lombok.RequiredArgsConstructor;

@Endpoint
@RequiredArgsConstructor
public class AccessManagementEndpointImpl implements AccessManagementEndpoint {

    private final ApplicationUserService applicationUserService;
    private final RoleService roleService;

    @Override
    public ApplicationUser getApplicationUserById(Long id) {
        return applicationUserService.getById(id);
    }

    @Override
    public Role getRoleById(Long id) {
        return roleService.getById(id);
    }

    @Override
    public Long saveRole(SaveRoleCommand command) {
        return roleService.save(command);
    }

    @Override
    public Long createApplicationUser(CreateApplicationUserCommand command) {
        return applicationUserService.create(command);
    }

    @Override
    public Long updateApplicationUser(UpdateApplicationUserCommand command) {
        return applicationUserService.update(command);
    }

    @Override
    public Long changeApplicationUserPassword(ChangePasswordCommand command) {
        return applicationUserService.changePassword(command);
    }

}
