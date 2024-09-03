package transcribe.domain.application_user.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import transcribe.domain.application_user.data.Role;

import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CreateApplicationUserCommand {

    @NotBlank
    private String username;

    @NotBlank
    private String name;

    @NotBlank
    private String password;

    @NotBlank
    private String passwordConfirmation;

    @NotNull
    @Builder.Default
    private Boolean enabled = true;

    @NotNull
    private Set<Role> roles;

}
