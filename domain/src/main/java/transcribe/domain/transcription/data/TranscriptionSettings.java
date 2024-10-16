package transcribe.domain.transcription.data;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.settings.Settings;

@Settings(key = "e9f82a87-d9bc-4ff5-8a41-f3c5624acfcb")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptionSettings {

    @Builder.Default
    @Min(1)
    private int averageRealTimeFactorWindow = 50_000;

    @Builder.Default
    @Min(0)
    private double realTimeFactorFallback = 1.0;

}
