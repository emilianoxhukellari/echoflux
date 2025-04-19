package echoflux.core.audio.splitter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CopyAudioCommand {

    @NotNull
    private Path source;

    @NotNull
    @Valid
    private AudioSegment audioSegment;

}
