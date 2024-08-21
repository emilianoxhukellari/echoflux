package transcribe.core.audio.ffmpeg;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import org.springframework.stereotype.Component;
import transcribe.core.properties.FFmpegProperties;

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
