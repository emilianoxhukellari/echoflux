package transcribe.domain.transcription.pipeline;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.transcription.data.TranscriptionEntity;

import java.util.Optional;

@Validated
public interface TranscriptionPipeline {

    /**
     * This method does not throw on error. Instead, it returns an empty optional.
     */
    Optional<TranscriptionEntity> transcribe(@Valid @NotNull TranscriptionPipelineCommand command);

}
