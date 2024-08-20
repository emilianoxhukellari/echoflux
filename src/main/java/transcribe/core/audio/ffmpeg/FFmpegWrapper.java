package transcribe.core.audio.ffmpeg;

import lombok.extern.slf4j.Slf4j;
import transcribe.config.properties.FFmpegProperties;
import lombok.SneakyThrows;
import net.bramp.ffmpeg.FFmpeg;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FFmpegWrapper {

    private final FFmpeg fFmpeg;

    public FFmpegWrapper(FFmpegProperties ffmpegProperties) {
        log.info("Loading FFmpeg from: [{}]", ffmpegProperties.getFfmpegExecutionPath());
        fFmpeg = newFFmpeg(ffmpegProperties.getFfmpegExecutionPath());
    }

    public FFmpeg ffmpeg() {
        return fFmpeg;
    }

    @SneakyThrows
    private static FFmpeg newFFmpeg(String path) {
        return new FFmpeg(path);
    }

}
