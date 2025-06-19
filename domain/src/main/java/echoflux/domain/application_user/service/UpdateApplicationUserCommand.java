package echoflux.domain.application_user.service;

import echoflux.core.core.country.Country;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import echoflux.domain.application_user.data.Role;

import java.time.ZoneId;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateApplicationUserCommand {

    @NotNull
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String name;

    @NotNull
    private Boolean enabled;

    @NotNull
    private Country country;

    @NotNull
    private ZoneId zoneId;

    @NotNull
    private Set<Role> roles;

}
