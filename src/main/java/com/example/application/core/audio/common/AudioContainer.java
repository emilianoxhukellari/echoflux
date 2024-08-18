package com.example.application.core.audio.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AudioContainer {

    OGG("libopus", "ogg"),
    MP3("libmp3lame", "mp3"),
    WAV("pcm_s16le", "wav");

    private final String codec;
    private final String container;

}
