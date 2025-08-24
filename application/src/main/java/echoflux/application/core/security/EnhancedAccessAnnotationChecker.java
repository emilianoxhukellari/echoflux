package echoflux.application.core.security;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import echoflux.core.core.validate.guard.Guard;
import echoflux.domain.core.security.ApplicationUser;
import echoflux.domain.core.security.RequiredPermissions;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.AnnotatedElement;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

public class EnhancedAccessAnnotationChecker {

    public static boolean hasAccess(AnnotatedElement annotatedElement, Principal principal) {
        Guard.notNull(annotatedElement, "annotatedElement");

        if (annotatedElement.isAnnotationPresent(DenyAll.class)) {
            return false;
        }

        if (annotatedElement.isAnnotationPresent(AnonymousAllowed.class)) {
            return true;
        }

        if (!(principal instanceof Authentication authentication)) {
            return false;
        }

        if (authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        if (!(authentication.getPrincipal() instanceof ApplicationUser applicationUser)) {
            return false;
        }

        var requiredPermissions = annotatedElement.getAnnotation(RequiredPermissions.class);
        if (requiredPermissions != null && applicationUser.hasAllPermissionsOrRoot(requiredPermissions.value())) {
            return true;
        }

        var rolesAllowed = annotatedElement.getAnnotation(RolesAllowed.class);

        if (rolesAllowed != null) {
            var allowedRoles = List.of(rolesAllowed.value());
            boolean allowed = applicationUser.getRoles()
                    .stream()
                    .anyMatch(allowedRoles::contains);

            if (allowed) {
                return true;
            }
        }

        return annotatedElement.isAnnotationPresent(PermitAll.class);
    }

    public static boolean hasAccess(AnnotatedElement annotatedElement) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        return hasAccess(annotatedElement, authentication);
    }

    public static boolean hasAnyAccess(AnnotatedElement... annotatedElements) {
        Objects.requireNonNull(annotatedElements, "annotatedElements");

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        for (var annotatedElement : annotatedElements) {
            if (hasAccess(annotatedElement, authentication)) {
                return true;
            }
        }

        return false;
    }

}
