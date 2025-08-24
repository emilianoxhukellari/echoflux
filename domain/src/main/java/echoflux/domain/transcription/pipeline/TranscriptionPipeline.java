package echoflux.domain.transcription.pipeline;

import echoflux.domain.jooq.tables.pojos.Transcription;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface TranscriptionPipeline {

    Transcription transcribe(@Valid @NotNull TranscriptionPipelineCommand command);

}
