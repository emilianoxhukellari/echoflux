package transcribe.domain.transcription.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface TranscriptionPipeline {

    void transcribeWithFeedback(@Valid @NotNull TranscriptionPipelineCommand command, @NotNull @Valid TranscriptionFeedback feedback);

}
