package transcribe.application.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.application.core.annotation.BigText;
import transcribe.core.core.annotation.Required;
import transcribe.annotation.jpa.JpaDto;
import transcribe.domain.template.data.TemplateEntity;

import java.time.LocalDateTime;

@JpaDto(entityBeanType = TemplateEntity.class)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateJpaDto {

    private Long id;

    @Required
    private String name;

    @Required
    @BigText
    private String content;

    private Integer version;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

}
