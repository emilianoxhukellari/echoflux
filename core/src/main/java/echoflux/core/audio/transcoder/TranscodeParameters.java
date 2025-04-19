package echoflux.core.audio.transcoder;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import echoflux.core.audio.common.AudioContainer;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscodeParameters {

    @NotNull
    @Builder.Default
    private AudioContainer audioContainer = AudioContainer.WEBM;

    @Min(1)
    @Builder.Default
    private int channels = 1;

}
