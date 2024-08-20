package transcribe.core.audio.ffmpeg;

import lombok.extern.slf4j.Slf4j;
import transcribe.config.properties.FFmpegProperties;
import lombok.SneakyThrows;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.stereotype.Component;

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
    private static FFprobe newFFprobe(String path) {
        return new FFprobe(path);
    }

}
