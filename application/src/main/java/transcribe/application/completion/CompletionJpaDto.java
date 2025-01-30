package transcribe.application.completion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.application.core.annotation.BigText;
import transcribe.core.core.annotation.Required;
import transcribe.annotation.jpa.JpaDto;
import transcribe.domain.completion.data.CompletionEntity;
import transcribe.domain.completion.data.CompletionStatus;

import java.time.LocalDateTime;

@JpaDto(entityBeanType = CompletionEntity.class)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompletionJpaDto {

    private Long id;

    @Required
    private Long transcriptionId;

    @Required
    @BigText
    private String input;

    @Required
    private String output;

    private Integer inputTokens;

    private Integer outputTokens;

    private String model;

    private Double temperature;

    private Double topP;

    @Required
    private CompletionStatus status;

    private Long durationMillis;

    @BigText
    private String error;

    private Integer version;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

}
