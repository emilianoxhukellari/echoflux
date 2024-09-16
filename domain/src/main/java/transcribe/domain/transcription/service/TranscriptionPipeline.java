package transcribe.domain.transcription.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.core.transcribe.common.TranscribeResult;
import transcribe.domain.transcription.data.TranscriptionStatus;

import java.util.Optional;

@Validated
public interface TranscriptionPipeline {

    /**
     * This method will not throw on error. Instead, the status of {@link TranscriptionStatus#FAILED} will be sent to the feedback.
     *
     * @return {@link TranscribeResult} if successful, empty otherwise.
     */
    Optional<TranscribeResult> transcribeWithFeedback(@Valid @NotNull TranscriptionPipelineCommand command,
                                                      @Valid @NotNull TranscriptionFeedback feedback);

}
