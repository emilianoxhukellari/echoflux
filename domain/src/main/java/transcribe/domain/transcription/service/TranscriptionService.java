package transcribe.domain.transcription.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.transcription.data.TranscriptionEntity;

@Validated
public interface TranscriptionService {

    TranscriptionEntity get(Long id);

    TranscriptionEntity create(@Valid @NotNull CreateTranscriptionCommand command);

    TranscriptionEntity patch(@Valid @NotNull PatchTranscriptionCommand command);

    TranscriptionEntity rename(@Valid @NotNull RenameTranscriptionCommand command);

    double getRealTimeFactor();

}
