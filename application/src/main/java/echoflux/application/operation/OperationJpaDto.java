package echoflux.application.operation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceCreator;
import echoflux.annotation.jpa.JpaDto;
import echoflux.application.core.annotation.BigText;
import echoflux.core.core.annotation.Required;
import echoflux.domain.operation.data.OperationEntity;
import echoflux.domain.operation.data.OperationStatus;
import echoflux.domain.operation.data.OperationType;

import java.time.Duration;
import java.time.LocalDateTime;

@JpaDto(entityBeanType = OperationEntity.class)
@Data
@Builder
@AllArgsConstructor(onConstructor_ = @PersistenceCreator)
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

    @Setter(AccessLevel.NONE)
    private Duration duration;

    @BigText
    private String description;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

}
