package transcribe.domain.transcription.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.transcription.data.TranscriptionEntity;
import transcribe.domain.transcription.data.TranscriptionProjection;

@Validated
public interface TranscriptionService {

    TranscriptionEntity getById(@NotNull Long id);

    TranscriptionEntity getByIdFetchWords(@NotNull Long id);

    TranscriptionProjection projectById(@NotNull Long id);

    TranscriptionProjection create(@Valid @NotNull CreateTranscriptionCommand command);

    TranscriptionProjection patch(@Valid @NotNull PatchTranscriptionCommand command);

    TranscriptionProjection rename(@Valid @NotNull RenameTranscriptionCommand command);

}
