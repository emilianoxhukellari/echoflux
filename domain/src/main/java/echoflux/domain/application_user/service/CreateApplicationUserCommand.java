package echoflux.domain.application_user.service;

import echoflux.core.core.country.Country;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import echoflux.domain.application_user.data.Role;
import echoflux.domain.application_user.service.impl.HasPassword;
import echoflux.domain.core.password.Password;
import echoflux.domain.core.password.PasswordMatch;

import java.time.ZoneId;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@PasswordMatch
public class CreateApplicationUserCommand implements HasPassword {

    @NotBlank
    private String username;

    @NotBlank
    private String name;

    @NotBlank
    @Password
    private String password;

    @NotBlank
    @Password
    private String passwordConfirmation;

    @NotNull
    @Builder.Default
    private Boolean enabled = true;

    @NotNull
    private Country country;

    @NotNull
    private ZoneId zoneId;

    @NotNull
    private Set<Role> roles;

}
