package transcribe.domain.completion.pipeline;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.core.provider.AiProvider;
import transcribe.core.settings.Settings;

@Settings(key = "182d89fe-7959-49eb-aac1-f0c7f739beba")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompletionsPipelineSettings {

    @Builder.Default
    @NotNull
    private AiProvider preferredAiProvider = AiProvider.OPENAI;

}
