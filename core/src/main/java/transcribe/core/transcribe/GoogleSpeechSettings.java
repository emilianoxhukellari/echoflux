package transcribe.core.transcribe;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.settings.Settings;

@Settings(key = "35b58f74-e873-47cf-b44d-c603bbc2ce23")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleSpeechSettings {

    @Builder.Default
    @NotBlank
    private String model = "chirp";

    @Builder.Default
    private boolean enableAutomaticPunctuation = true;

    @Builder.Default
    @Positive
    private int initialRetryDelayDurationSeconds = 30;

    @Builder.Default
    @Positive
    private float retryDelayMultiplier = 1.5f;

    @Builder.Default
    @Positive
    private int maxRetryDelayDurationSeconds = 60;

    @Builder.Default
    @Positive
    private int totalTimeoutDurationMinutes = 60;

}
