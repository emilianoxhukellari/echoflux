package echoflux.application.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.PersistenceCreator;
import echoflux.annotation.jpa.JpaDto;
import echoflux.application.core.annotation.BigText;
import echoflux.core.core.annotation.Required;
import echoflux.domain.template.data.TemplateEntity;

import java.time.LocalDateTime;

@JpaDto(entityBeanType = TemplateEntity.class)
@Data
@Builder
@AllArgsConstructor(onConstructor_ = @PersistenceCreator)
@NoArgsConstructor
public class TemplateJpaDto {

    private Long id;

    @Required
    private String name;

    @Required
    @BigText
    private String content;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

}
