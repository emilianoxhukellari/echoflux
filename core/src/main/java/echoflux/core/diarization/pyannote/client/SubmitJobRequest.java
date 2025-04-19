package echoflux.core.diarization.pyannote.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record SubmitJobRequest(@JsonProperty("url") @NotBlank String url) {
}
