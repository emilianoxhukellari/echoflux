package transcribe.application.settings;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.PersistenceCreator;
import transcribe.annotation.jpa.JpaDto;
import transcribe.core.core.annotation.Required;
import transcribe.domain.settings.data.SettingsEntity;

import java.time.LocalDateTime;

@JpaDto(entityBeanType = SettingsEntity.class)
@Data
@Builder
@AllArgsConstructor(onConstructor_ = @PersistenceCreator)
@NoArgsConstructor
public class SettingsJpaDto {

    private Long id;

    @Required
    private String key;

    @Required
    private String name;

    @Required
    private JsonNode value;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

}
