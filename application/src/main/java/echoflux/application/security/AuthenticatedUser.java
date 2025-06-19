package echoflux.application.security;

import echoflux.core.core.utils.MoreSets;
import echoflux.domain.application_user.data.ApplicationUser;
import echoflux.domain.application_user.data.Role;
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

    public static boolean hasRole(Role role) {
        return find()
                .map(u -> MoreSets.contains(u.getRoles(), role))
                .orElse(false);
    }

    public static boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    public static Optional<Authentication> findAuthentication() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        return Optional.ofNullable(authentication);
    }

}
