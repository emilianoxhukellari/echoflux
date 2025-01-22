package transcribe.domain.transcription.pipeline;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.transcription.data.TranscriptionEntity;

@Validated
public interface TranscriptionPipeline {

    TranscriptionEntity transcribe(@Valid @NotNull TranscriptionPipelineCommand command);

}
