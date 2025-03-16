package transcribe.core.diarization.pyannote.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

public record GetJobResponse(@JsonProperty("jobId") String jobId,
                             @JsonProperty("status") DiarizationJobStatus status,
                             @JsonProperty("createdAt") OffsetDateTime createdAt,
                             @JsonProperty("updatedAt") OffsetDateTime updatedAt,
                             @JsonProperty("output") Output output) {

    public record Output(@JsonProperty("diarization") List<DiarizationEntry> diarization) {
    }

    public record DiarizationEntry(@JsonProperty("speaker") String speaker,
                                   @JsonProperty("start") double start,
                                   @JsonProperty("end") double end) {
    }

}
