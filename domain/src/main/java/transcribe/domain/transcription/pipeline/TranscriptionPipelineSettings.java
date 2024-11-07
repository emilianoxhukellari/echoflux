package transcribe.domain.transcription.pipeline;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.settings.Settings;

@Settings(key = "9714d1bb-4d3d-40dd-8a96-2e0eb1350d83")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptionPipelineSettings {

    @Builder.Default
    @NotBlank
    private String enhanceCompletionDynamicTemplateName = "enhance-transcription";

    @Builder.Default
    @NotBlank
    private String enhanceCompletionTextDataModelKey = "text";

}
