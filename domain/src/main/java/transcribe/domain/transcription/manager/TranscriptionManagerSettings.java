package transcribe.domain.transcription.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.settings.Settings;

@Settings(key = "4012925e-5f88-4e31-ac80-fc8572e6b428")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptionManagerSettings {

    @Builder.Default
    private String emptySpeakerName = "Unknown Speaker";

}
