package echoflux.application.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.PersistenceCreator;
import echoflux.annotation.core.AttributeOverride;
import echoflux.annotation.jpa.JpaDto;
import echoflux.annotation.projection.AttributeProjectType;
import echoflux.core.core.annotation.Required;
import echoflux.domain.application_user.data.ApplicationUserEntity;
import echoflux.domain.application_user.data.Role;

import java.time.LocalDateTime;
import java.util.Set;

@JpaDto(
        entityBeanType = ApplicationUserEntity.class,
        hiddenFields = {ApplicationUserJpaDto_.PASSWORD}
)
@Data
@Builder
@AllArgsConstructor(onConstructor_ = @PersistenceCreator)
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
    @AttributeOverride(projectType = AttributeProjectType.DEFERRED)
    private Set<Role> roles;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

}
