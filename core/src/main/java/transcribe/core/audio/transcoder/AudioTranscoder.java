package transcribe.core.audio.transcoder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;

@Validated
public interface AudioTranscoder {

    Path transcode(@NotNull Path source, @Valid @NotNull TranscodeParameters command);

}
