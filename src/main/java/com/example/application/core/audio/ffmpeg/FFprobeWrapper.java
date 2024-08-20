package com.example.application.core.audio.ffmpeg;

import com.example.application.config.properties.FFmpegProperties;
import lombok.SneakyThrows;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.stereotype.Component;

@Component
public class FFprobeWrapper {

    private final FFprobe fFprobe;

    public FFprobeWrapper(FFmpegProperties fFmpegProperties) {
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
