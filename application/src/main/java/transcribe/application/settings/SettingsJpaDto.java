package transcribe.application.settings;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.core.annotation.Required;
import transcribe.annotation.jpa.JpaDto;
import transcribe.domain.settings.data.SettingsEntity;

import java.time.LocalDateTime;

@JpaDto(entityBeanType = SettingsEntity.class)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettingsJpaDto {

    private Long id;

    @Required
    private String key;

    @Required
    private String name;

    @Required
    private JsonNode value;

    private Integer version;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

}
