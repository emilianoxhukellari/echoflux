package echoflux.domain.transcription.pipeline;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import echoflux.domain.transcription.data.TranscriptionProjection;

@Validated
public interface TranscriptionPipeline {

    TranscriptionProjection transcribe(@Valid @NotNull TranscriptionPipelineCommand command);

}
