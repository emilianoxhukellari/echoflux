package transcribe.core.audio.transcoder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.core.core.temp_file.TempFileNameGenerator;

import java.nio.file.Path;

@Validated
public interface AudioTranscoder extends TempFileNameGenerator {

    Path transcode(@NotNull Path source, @Valid @NotNull TranscodeParameters command);

}
