package com.example.application.core.audio.ffmpeg;

import com.example.application.config.properties.FFmpegProperties;
import lombok.SneakyThrows;
import net.bramp.ffmpeg.FFmpeg;
import org.springframework.stereotype.Component;

@Component
public class FFmpegWrapper {

    private final FFmpeg fFmpeg;

    public FFmpegWrapper(FFmpegProperties ffmpegProperties) {
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
