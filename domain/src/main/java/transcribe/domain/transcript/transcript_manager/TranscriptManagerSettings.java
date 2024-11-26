package transcribe.domain.transcript.transcript_manager;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.settings.Settings;

@Settings(key = "9ad694ae-4186-4fdc-b624-1cba2b6d475a")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptManagerSettings {

    @Builder.Default
    @NotNull
    private Integer maxWordsPerPart = 30;

}
