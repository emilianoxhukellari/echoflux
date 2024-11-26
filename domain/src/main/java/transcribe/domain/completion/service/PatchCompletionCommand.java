package transcribe.domain.completion.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import transcribe.core.core.constraint.float_range.FloatRange;
import transcribe.domain.completion.data.CompletionStatus;

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
    private Integer inputTokens;

    @Min(0)
    private Integer outputTokens;

    private String model;

    @FloatRange(min = 0.1f, max = 2.0f)
    private Float temperature;

    @FloatRange(min = 0.1f, max = 1.0f)
    private Float topP;

    @Min(0)
    @With
    private Long durationMillis;

    private String error;

}
