package transcribe.domain.application_user.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.domain.application_user.data.Role;

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
    private Set<Role> roles;

}
