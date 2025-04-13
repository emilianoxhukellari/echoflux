package transcribe.application.completion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.PersistenceCreator;
import transcribe.annotation.core.ParentProperty;
import transcribe.annotation.jpa.JpaDto;
import transcribe.application.core.annotation.BigText;
import transcribe.application.transcription.TranscriptionJpaDto;
import transcribe.core.core.annotation.Required;
import transcribe.domain.completion.data.CompletionEntity;
import transcribe.domain.completion.data.CompletionStatus;

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
