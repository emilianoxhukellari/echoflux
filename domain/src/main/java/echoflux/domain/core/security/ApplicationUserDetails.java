package echoflux.domain.core.security;

import echoflux.core.core.country.Country;
import echoflux.core.core.validate.guard.Guard;
import lombok.Builder;

import java.time.ZoneId;
import java.util.Set;

@Builder
public record ApplicationUserDetails(Long id,
                                     String username,
                                     String name,
                                     String password,
                                     boolean enabled,
                                     Country country,
                                     ZoneId zoneId,
                                     Set<String> roles,
                                     Set<PermissionType> permissions) {

    public ApplicationUserDetails {
        Guard.notNull(id, "id");
        Guard.notBlank(username, "username");
        Guard.notBlank(name, "name");
        Guard.notBlank(password, "password");
        Guard.notNull(country, "country");
        Guard.notNull(zoneId, "zoneId");
        Guard.notNull(roles, "roles");
        Guard.notNull(permissions, "permissions");
    }

}
