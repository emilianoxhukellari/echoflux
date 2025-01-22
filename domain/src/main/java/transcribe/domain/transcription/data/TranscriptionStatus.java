package transcribe.domain.transcription.data;

public enum TranscriptionStatus {

    CREATED,
    DOWNLOADING_PUBLIC,
    FINDING_DURATION,
    PROCESSING,
    TRANSCRIBING,
    ENHANCING,
    ENHANCING_FAILED,
    COMPLETED,
    FAILED

}
