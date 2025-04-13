package transcribe.domain.completion.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import transcribe.core.core.validate.constraint.double_range.DoubleRange;
import transcribe.core.core.validate.constraint.duration.PositiveOrZeroDuration;
import transcribe.domain.completion.data.CompletionStatus;

import java.time.Duration;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchCompletionCommand {

    @NotNull
    @With
    private Long id;

    @With
    private CompletionStatus status;

    private String output;

    @Min(0)
    private Long inputTokens;

    @Min(0)
    private Long outputTokens;

    private String model;

    @DoubleRange(min = 0.1d, max = 2.0d)
    private Double temperature;

    @DoubleRange(min = 0.1d, max = 1.0d)
    private Double topP;

    @PositiveOrZeroDuration
    private Duration duration;

    private String error;

}
