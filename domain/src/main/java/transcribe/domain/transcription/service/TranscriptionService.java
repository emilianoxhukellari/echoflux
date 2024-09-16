package transcribe.domain.transcription.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.transcription.data.TranscriptionEntity;
import transcribe.domain.transcription.data.TranscriptionStatus;

@Validated
public interface TranscriptionService {

    TranscriptionEntity create(@Valid @NotNull CreateTranscriptionCommand command);

    TranscriptionEntity update(@Valid @NotNull UpdateTranscriptionCommand command);

    TranscriptionEntity updateStatus(Long id, TranscriptionStatus status);

    TranscriptionEntity get(@NotNull Long id);

}
