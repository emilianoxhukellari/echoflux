package echoflux.core.completions;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import echoflux.core.core.validate.constraint.double_range.DoubleRange;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompletionResult {

    private String output;

    @Min(0)
    @NotNull
    private Long inputTokens;

    @Min(0)
    @NotNull
    private Long outputTokens;

    @NotBlank
    private String model;

    @DoubleRange(min = 0.1d, max = 2.0d)
    @NotNull
    private Double temperature;

    @DoubleRange(min = 0.1d, max = 1.0d)
    @NotNull
    private Double topP;

}
