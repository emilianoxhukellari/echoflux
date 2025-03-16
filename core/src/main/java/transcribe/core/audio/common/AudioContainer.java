package transcribe.core.audio.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AudioContainer {

    OGG("libopus", "ogg", "audio/ogg"),
    WEBM("libopus", "webm", "audio/webm"),
    MP3("libmp3lame", "mp3", "audio/mpeg"),
    WAV("pcm_s16le", "wav", "audio/wav"),
    FLAC("flac", "flac", "audio/flac");

    private final String codec;
    private final String container;
    private final String contentType;

}