package transcribe.application.operation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.application.core.annotation.BigText;
import transcribe.core.core.annotation.Required;
import transcribe.annotation.jpa.JpaDto;
import transcribe.domain.operation.data.OperationEntity;
import transcribe.domain.operation.data.OperationStatus;
import transcribe.domain.operation.data.OperationType;

import java.time.LocalDateTime;

@JpaDto(entityBeanType = OperationEntity.class)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperationJpaDto {

    private Long id;

    @Required
    private String name;

    @Required
    private OperationType type;

    @Required
    private OperationStatus status;

    @BigText
    private String error;

    @Required
    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    private Double durationSeconds;

    @BigText
    private String description;

    private Integer version;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

}
