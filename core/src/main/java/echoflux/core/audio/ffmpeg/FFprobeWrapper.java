package echoflux.core.audio.ffmpeg;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.stereotype.Component;
import echoflux.core.properties.FFmpegProperties;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;

@Component
@Slf4j
public class FFprobeWrapper {

    private final FFprobe fFprobe;

    public FFprobeWrapper(FFmpegProperties fFmpegProperties) {
        log.info("Loading FFprobe from: [{}]", fFmpegProperties.getFfprobeExecutionPath());
        fFprobe = newFFprobe(fFmpegProperties.getFfprobeExecutionPath());
    }

    public FFprobe ffprobe() {
        return fFprobe;
    }

    @SneakyThrows
    public Duration getDuration(Path audio) {
        Objects.requireNonNull(audio, "audio is required");

        var seconds = ffprobe().probe(audio.toAbsolutePath().toString()).format.duration;
        var millis = (long) (seconds * 1000);

        return Duration.ofMillis(millis);
    }

    @SneakyThrows
    private static FFprobe newFFprobe(String path) {
        return new FFprobe(path);
    }

}
