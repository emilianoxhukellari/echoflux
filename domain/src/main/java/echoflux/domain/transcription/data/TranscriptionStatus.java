package echoflux.domain.transcription.data;

public enum TranscriptionStatus {

    CREATED,
    DOWNLOADING_PUBLIC,
    FINDING_DURATION,
    TRANSCODING,
    UPLOADING,
    SPLITTING,
    TRANSCRIBING,
    ENHANCING,
    COMPLETED,
    FAILED

}
