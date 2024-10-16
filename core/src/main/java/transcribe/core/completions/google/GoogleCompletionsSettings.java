package transcribe.core.completions.google;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.core.constraint.float_range.FloatRange;
import transcribe.core.settings.Settings;

@Settings(key = "c4da9223-ddd2-43cf-86b1-c8f9210ae609")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleCompletionsSettings {

    @Builder.Default
    @NotBlank
    private String continueMessage = "continue";

    @Builder.Default
    @NotBlank
    private String model = "gemini-1.5-pro-002";

    @Builder.Default
    @FloatRange(min = 0.1f, max = 2.0f)
    private float temperature = 0.9f;

    @Builder.Default
    @FloatRange(min = 0.1f, max = 1.0f)
    private float topP = 0.9f;

}
