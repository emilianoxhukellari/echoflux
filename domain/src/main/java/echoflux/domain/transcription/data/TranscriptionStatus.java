package echoflux.domain.transcription.data;

public enum TranscriptionStatus {

    CREATED,
    DOWNLOADING_PUBLIC,
    FINDING_DURATION,
    TRANSCODING,
    TRANSCRIBING,
    ENHANCING,
    SUCCESS,
    FAILED

}
