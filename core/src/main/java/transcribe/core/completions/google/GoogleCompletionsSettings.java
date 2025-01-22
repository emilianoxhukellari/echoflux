package transcribe.core.completions.google;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.core.validate.constraint.double_range.DoubleRange;
import transcribe.core.settings.Settings;

@Settings(key = "c4da9223-ddd2-43cf-86b1-c8f9210ae609")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleCompletionsSettings {

    @Builder.Default
    @NotBlank
    private String continuePhrase = "continue";

    @Builder.Default
    @NotBlank
    private String model = "gemini-1.5-pro-002";

    @Builder.Default
    @DoubleRange(min = 0.1d, max = 2.0d)
    @NotNull
    private Double temperature = 0.8d;

    @Builder.Default
    @DoubleRange(min = 0.1d, max = 1.0d)
    @NotNull
    private Double topP = 0.9d;

}
