package echoflux.core.transcribe.google;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import echoflux.core.settings.Settings;

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
    @Positive
    @NotNull
    private Integer initialRetryDelayDurationSeconds = 30;

    @Builder.Default
    @Positive
    @NotNull
    private Float retryDelayMultiplier = 1.5f;

    @Builder.Default
    @Positive
    @NotNull
    private Integer maxRetryDelayDurationSeconds = 60;

    @Builder.Default
    @Positive
    @NotNull
    private Integer totalTimeoutDurationMinutes = 60;

}
