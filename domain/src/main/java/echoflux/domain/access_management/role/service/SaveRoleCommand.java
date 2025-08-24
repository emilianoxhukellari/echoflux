package echoflux.domain.access_management.role.service;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaveRoleCommand {

    @Nullable
    private Long id;

    @NotBlank
    private String name;

    @Nullable
    private String description;

    @NotNull
    private Set<@NotNull Long> permissionIds;

}
