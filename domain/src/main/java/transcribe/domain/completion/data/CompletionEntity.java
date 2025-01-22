package transcribe.domain.completion.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import transcribe.core.core.validate.constraint.double_range.DoubleRange;
import transcribe.domain.audit.data.BaseEntity;

@Entity
@Table(name = "completion")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CompletionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "transcription_id")
    @NotNull
    private Long transcriptionId;

    @Column(name = "input")
    @NotEmpty
    private String input;

    @Column(name = "output")
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
    @DoubleRange(min = 0.1d, max = 2.0d)
    private Double temperature;

    @Column(name = "top_p")
    @DoubleRange(min = 0.1d, max = 1.0d)
    private Double topP;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private CompletionStatus status;

    @Column(name = "duration_millis")
    @Min(0)
    private Long durationMillis;

    @Column(name = "error")
    private String error;

}
