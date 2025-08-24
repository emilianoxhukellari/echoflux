package echoflux.domain.core.security;

import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component("permissionAccess")
public class PermissionAccess {

    public boolean check(Object principal, PermissionType... permissions) {
        if (permissions == null) {
            return true;
        }

        if (principal instanceof ApplicationUser applicationUser) {
            return applicationUser.hasAllPermissionsOrRoot(permissions);
        }

        return false;
    }

}
