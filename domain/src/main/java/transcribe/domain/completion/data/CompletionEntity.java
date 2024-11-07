package transcribe.domain.completion.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import transcribe.core.core.constraint.float_range.FloatRange;
import transcribe.domain.audit.data.AuditEntity;
import transcribe.domain.core.annotation.BigText;

@Entity
@Table(name = "completion")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CompletionEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "input")
    @NotEmpty
    @BigText
    private String input;

    @Column(name = "output")
    @BigText
    private String output;

    @Column(name = "input_tokens")
    @Min(0)
    private Integer inputTokens;

    @Column(name = "output_tokens")
    @Min(0)
    private Integer outputTokens;

    @Column(name = "model")
    private String model;

    @Column(name = "temperature")
    @FloatRange(min = 0.1f, max = 2.0f)
    private Float temperature;

    @Column(name = "top_p")
    @FloatRange(min = 0.1f, max = 1.0f)
    private Float topP;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private CompletionStatus status;

    @Column(name = "duration_millis")
    @Min(0)
    private Long durationMillis;

    @Column(name = "error")
    @BigText
    private String error;

}
