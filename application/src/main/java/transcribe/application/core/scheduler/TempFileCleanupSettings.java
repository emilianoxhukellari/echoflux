package transcribe.application.core.scheduler;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.settings.Settings;

@Settings(key = "884d3757-5f4a-4899-8a58-d72d48f493c1")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TempFileCleanupSettings {

    @Builder.Default
    @Min(0)
    private int deleteAfterMinutes = 30;

}
