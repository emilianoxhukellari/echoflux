package echoflux.domain.application_user.data;

import echoflux.core.core.country.Country;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.ZoneId;
import java.util.Objects;
import java.util.Set;

@Getter
public class ApplicationUser extends User {

    private final Long id;
    private final String name;
    private final Set<Role> roles;
    private final Country country;
    private final ZoneId zoneId;

    public ApplicationUser(ApplicationUserProjection applicationUserProjection) {
        Objects.requireNonNull(applicationUserProjection, "ApplicationUserProjection");

        var authorities = applicationUserProjection.getRoles()
                .stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .toList();

        super(
                applicationUserProjection.getUsername(),
                applicationUserProjection.getPassword(),
                applicationUserProjection.getEnabled(),
                true,
                true,
                true,
                authorities
        );

        this.id = applicationUserProjection.getId();
        this.name = applicationUserProjection.getName();
        this.roles = applicationUserProjection.getRoles();
        this.country = applicationUserProjection.getCountry();
        this.zoneId = applicationUserProjection.getZoneId();
    }

}