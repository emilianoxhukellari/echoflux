package echoflux.domain.operation.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import echoflux.core.core.validate.constraint.duration.PositiveOrZeroDuration;
import echoflux.domain.audit.data.BaseEntity;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "operation")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OperationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @NotBlank
    private String name;

    @Column(name = "type")
    @NotNull
    @Enumerated(EnumType.STRING)
    private OperationType type;

    @Column(name = "status")
    @NotNull
    @Enumerated(EnumType.STRING)
    private OperationStatus status;

    @Column(name = "error")
    private String error;

    @Column(name = "started_at")
    @NotNull
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Formula("ended_at - started_at")
    @JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
    @PositiveOrZeroDuration
    private Duration duration;

    @Column(name = "description")
    private String description;

}
