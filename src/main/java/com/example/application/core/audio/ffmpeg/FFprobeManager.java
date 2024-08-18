package com.example.application.core.audio.ffmpeg;

import lombok.SneakyThrows;
import net.bramp.ffmpeg.FFprobe;

public final class FFprobeManager {

    private static final FFprobe FFPROBE = newFFprobe();

    public static FFprobe ffprobe() {
        return FFPROBE;
    }

    @SneakyThrows
    private static FFprobe newFFprobe() {
        return new FFprobe();
    }

}
