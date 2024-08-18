package com.example.application.core.audio.ffmpeg;

import lombok.SneakyThrows;
import net.bramp.ffmpeg.FFmpeg;

public final class FFmpegManager {

    private static final FFmpeg FFMPEG = newFFmpeg();

    public static FFmpeg ffmpeg() {
        return FFMPEG;
    }

    @SneakyThrows
    private static FFmpeg newFFmpeg() {
        return new FFmpeg();
    }

}
