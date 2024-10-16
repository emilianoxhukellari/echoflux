package transcribe.core.completions;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.core.constraint.float_range.FloatRange;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompletionResult {

    private String output;

    @Min(0)
    private int inputTokens;

    @Min(0)
    private int outputTokens;

    @NotBlank
    private String model;

    @FloatRange(min = 0.1f, max = 2.0f)
    private float temperature;

    @FloatRange(min = 0.1f, max = 1.0f)
    private float topP;

}
