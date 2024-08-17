package com.example.application.core.ffmpeg;

import lombok.SneakyThrows;
import net.bramp.ffmpeg.FFprobe;

public class FFprobeFactory {

    private static final FFprobe FFPROBE = newFFprobe();

    public static FFprobe ffprobe() {
        return FFPROBE;
    }

    @SneakyThrows
    private static FFprobe newFFprobe() {
        return new FFprobe();
    }

}
