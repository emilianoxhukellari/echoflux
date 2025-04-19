package echoflux.core.diarization.pyannote.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiarizationJobStatus {

    @JsonProperty("pending")
    PENDING(false),

    @JsonProperty("created")
    CREATED(false),

    @JsonProperty("succeeded")
    SUCCEEDED(true),

    @JsonProperty("canceled")
    CANCELED(true),

    @JsonProperty("failed")
    FAILED(true),

    @JsonProperty("running")
    RUNNING(false);

    private final boolean completed;

}
