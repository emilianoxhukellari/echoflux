package transcribe.core.diarization.pyannote.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface PyannoteDiarizationClient {

    SubmitJobResponse submitJob(@NotNull @Valid SubmitJobRequest request);

    GetJobResponse getJob(@NotBlank String jobId);

}
