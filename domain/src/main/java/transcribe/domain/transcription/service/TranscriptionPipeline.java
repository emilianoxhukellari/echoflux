package transcribe.domain.transcription.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.core.transcribe.common.TranscribeResult;
import transcribe.domain.transcription.data.TranscriptionStatus;
import transcribe.domain.transcription.event.TranscriptionStatusChangeUserEvent;

import java.util.Optional;

@Validated
public interface TranscriptionPipeline {

    /**
     * This method does not throw on error. Instead, it emits {@link TranscriptionStatusChangeUserEvent}
     * with status {@link TranscriptionStatus#FAILED}.
     *
     * @return {@link TranscribeResult} if successful, empty otherwise.
     */
    Optional<TranscribeResult> transcribe(@Valid @NotNull TranscriptionPipelineCommand command);

}
