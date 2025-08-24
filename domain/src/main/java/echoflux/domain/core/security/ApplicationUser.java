package echoflux.domain.core.security;

import echoflux.core.core.country.Country;
import echoflux.core.core.validate.guard.Guard;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Set;

@Getter
public class ApplicationUser extends User {

    private final Long id;
    private final String name;
    private final Country country;
    private final ZoneId zoneId;
    private final Set<String> roles;
    private final Set<PermissionType> permissions;

    public ApplicationUser(ApplicationUserDetails details) {
        Guard.notNull(details, "details");

        var grantedAuthorities = new ArrayList<GrantedAuthority>();

        for (var role : details.roles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        for (var permission : details.permissions()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(permission.name()));
        }

        super(
                details.username(),
                details.password(),
                details.enabled(),
                true,
                true,
                true,
                grantedAuthorities
        );

        this.id = details.id();
        this.name = details.name();
        this.roles = details.roles();
        this.country = details.country();
        this.zoneId = details.zoneId();
        this.permissions = details.permissions();
    }

    public boolean hasPermissionOrRoot(PermissionType permissions) {
        Guard.notNull(permissions, "permission");

        return this.permissions.contains(permissions) || this.permissions.contains(PermissionType.ROOT);
    }

    public boolean hasAllPermissionsOrRoot(PermissionType... permissions) {
        Guard.notNull(permissions, "permissions");

        for (var permission : permissions) {
            if (!hasPermissionOrRoot(permission)) {
                return false;
            }
        }

        return true;
    }

    public boolean hasAnyPermissionsOrRoot(PermissionType... permissions) {
        Guard.notNull(permissions, "permissions");

        for (var permission : permissions) {
            if (hasPermissionOrRoot(permission)) {
                return true;
            }
        }

        return false;
    }

}