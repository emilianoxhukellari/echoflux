package echoflux.application.core.security;

import echoflux.core.core.validate.guard.Guard;
import echoflux.domain.core.security.ApplicationUser;
import echoflux.domain.core.security.PermissionType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.ZoneId;
import java.util.Optional;

public final class AuthenticatedUser {

    public static boolean isPresent() {
        return find()
                .isPresent();
    }

    public static Optional<ApplicationUser> find() {
        return findAuthentication()
                .filter(a -> !(a instanceof AnonymousAuthenticationToken))
                .map(Authentication::getPrincipal)
                .map(ApplicationUser.class::cast);
    }

    public static ApplicationUser get() {
        return find()
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    public static Long getId() {
        return get()
                .getId();
    }

    public static ZoneId getZoneId() {
        return get()
                .getZoneId();
    }

    /**
     * Returns true if the user has the specified permission or {@link PermissionType#ROOT}.
     * */
    public static boolean checkPermission(PermissionType permissionType) {
        Guard.notNull(permissionType, "permission");

        return find()
                .map(u -> u.hasPermissionOrRoot(permissionType))
                .orElse(false);
    }

    public static boolean checkAllPermissions(PermissionType... permissions) {
        Guard.notNull(permissions, "permissions");

        return find()
                .map(u -> u.hasAllPermissionsOrRoot(permissions))
                .orElse(false);
    }

    public static boolean checkAnyPermissions(PermissionType... permissions) {
        Guard.notNull(permissions, "permissions");

        return find()
                .map(u -> u.hasAnyPermissionsOrRoot(permissions))
                .orElse(false);
    }

    public static Optional<Authentication> findAuthentication() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        return Optional.ofNullable(authentication);
    }

}
