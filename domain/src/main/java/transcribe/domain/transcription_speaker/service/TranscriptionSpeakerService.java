package transcribe.domain.transcription_speaker.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.transcription_speaker.data.TranscriptionSpeakerEntity;

@Validated
public interface TranscriptionSpeakerService {

    TranscriptionSpeakerEntity get(@NotNull Long id);

    TranscriptionSpeakerEntity create(@Valid @NotNull CreateTranscriptionSpeakerCommand command);

    TranscriptionSpeakerEntity rename(@Valid @NotNull RenameTranscriptionSpeakerCommand command);

    void delete(@NotNull Long id);

}
