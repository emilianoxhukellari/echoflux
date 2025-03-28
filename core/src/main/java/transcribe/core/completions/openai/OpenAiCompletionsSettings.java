package transcribe.core.completions.openai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.core.validate.constraint.double_range.DoubleRange;
import transcribe.core.settings.Settings;

@Settings(key = "dadf6784-95ae-4729-8390-22be6ba89597")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenAiCompletionsSettings {

    @Builder.Default
    @NotBlank
    private String continuePhrase = "continue";

    @Builder.Default
    @NotBlank
    private String assistantId = "asst_5oooQJrdJ09bznvndO5xpsAb";

    @Builder.Default
    @NotBlank
    private String model = "gpt-4o";

    @Builder.Default
    @DoubleRange(min = 0.1d, max = 2.0d)
    @NotNull
    private Double temperature = 0.9d;

    @Builder.Default
    @DoubleRange(min = 0.1d, max = 1.0d)
    @NotNull
    private Double topP = 0.7d;

    @Builder.Default
    @NotNull
    @Positive
    private Integer runMaxOutputTokens = 10000;

}
