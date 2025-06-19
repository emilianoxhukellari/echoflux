package echoflux.domain.transcription.pipeline;

import echoflux.domain.transcription.data.ScalarTranscriptionProjection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface TranscriptionPipeline {

    ScalarTranscriptionProjection transcribe(@Valid @NotNull TranscriptionPipelineCommand command);

}
