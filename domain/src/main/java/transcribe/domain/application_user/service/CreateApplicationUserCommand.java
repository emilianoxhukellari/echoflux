package transcribe.domain.application_user.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import transcribe.domain.application_user.data.Role;
import transcribe.domain.application_user.service.impl.HasPassword;
import transcribe.domain.core.password.Password;
import transcribe.domain.core.password.PasswordMatch;

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
    private Set<Role> roles;

}
