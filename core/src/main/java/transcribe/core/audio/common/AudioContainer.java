package transcribe.core.audio.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AudioContainer {

    OGG("libopus", "ogg"),
    WEBM("libopus", "webm"),
    MP3("libmp3lame", "mp3"),
    WAV("pcm_s16le", "wav"),
    FLAC("flac", "flac");

    private final String codec;
    private final String container;

}