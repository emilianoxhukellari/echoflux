package transcribe.core.diarization.pyannote.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubmitJobResponse(@JsonProperty("jobId") String jobId,
                                @JsonProperty("status") DiarizationJobStatus status) {
}
