package echoflux.application.completion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.PersistenceCreator;
import echoflux.annotation.core.ParentProperty;
import echoflux.annotation.jpa.JpaDto;
import echoflux.application.core.annotation.BigText;
import echoflux.application.transcription.TranscriptionJpaDto;
import echoflux.core.core.annotation.Required;
import echoflux.domain.completion.data.CompletionEntity;
import echoflux.domain.completion.data.CompletionStatus;

import java.time.Duration;
import java.time.LocalDateTime;

@JpaDto(entityBeanType = CompletionEntity.class)
@Data
@Builder
@AllArgsConstructor(onConstructor_ = @PersistenceCreator)
@NoArgsConstructor
public class CompletionJpaDto {

    private Long id;

    @Required
    @BigText
    private String input;

    @Required
    @BigText
    private String output;

    private Integer inputTokens;

    private Integer outputTokens;

    private String model;

    private Double temperature;

    private Double topP;

    @Required
    private CompletionStatus status;

    private Duration duration;

    @BigText
    private String error;

    @Required
    @ParentProperty
    private TranscriptionJpaDto transcription;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

}
