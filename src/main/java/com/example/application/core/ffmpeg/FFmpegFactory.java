package com.example.application.core.ffmpeg;

import lombok.SneakyThrows;
import net.bramp.ffmpeg.FFmpeg;

public final class FFmpegFactory {

    private static final FFmpeg FFMPEG = newFFmpeg();

    public static FFmpeg ffmpeg() {
        return FFMPEG;
    }

    @SneakyThrows
    private static FFmpeg newFFmpeg() {
        return new FFmpeg();
    }

}
