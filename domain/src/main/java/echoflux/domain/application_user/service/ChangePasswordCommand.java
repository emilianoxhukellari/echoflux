package echoflux.domain.application_user.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import echoflux.domain.application_user.service.impl.HasPassword;
import echoflux.domain.core.password.Password;
import echoflux.domain.core.password.PasswordMatch;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@PasswordMatch
public class ChangePasswordCommand implements HasPassword {

    @NotNull
    private Long id;

    @NotBlank
    @Password
    private String password;

    @NotBlank
    @Password
    private String passwordConfirmation;

}
