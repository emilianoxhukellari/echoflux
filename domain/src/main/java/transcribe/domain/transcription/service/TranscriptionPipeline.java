package transcribe.domain.transcription.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.core.transcribe.common.TranscribeResult;

import java.util.Optional;

@Validated
public interface TranscriptionPipeline {

    /**
     * This method does not throw on error. Instead, it returns an empty optional.
     *
     * @return {@link TranscribeResult} if successful, empty otherwise.
     */
    Optional<TranscribeResult> transcribe(@Valid @NotNull TranscriptionPipelineCommand command);

}
