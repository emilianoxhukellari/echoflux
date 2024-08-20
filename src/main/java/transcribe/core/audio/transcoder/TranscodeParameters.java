package transcribe.core.audio.transcoder;

import transcribe.core.audio.common.AudioContainer;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscodeParameters {

    @NotNull
    @Builder.Default
    private AudioContainer audioContainer = AudioContainer.OGG;

    @Min(1)
    @Builder.Default
    private int channels = 1;

}
