package transcribe.application.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.core.annotation.Required;
import transcribe.annotation.jpa.JpaDto;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.application_user.data.Role;

import java.time.LocalDateTime;
import java.util.Set;

@JpaDto(
        entityBeanType = ApplicationUserEntity.class,
        hiddenFields = {"password"}
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUserJpaDto {

    private Long id;

    @Required
    private String username;

    @Required
    private String name;

    @Required
    private String password;

    @Required
    private Boolean enabled;

    @Required
    private Set<Role> roles;

    private Integer version;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

}
