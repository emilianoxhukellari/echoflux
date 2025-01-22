package transcribe.domain.transcription_word.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.transcription_word.data.TranscriptionWordEntity;

import java.util.List;

@Validated
public interface TranscriptionWordService {

    TranscriptionWordEntity get(@NotNull Long id);

    TranscriptionWordEntity create(@Valid @NotNull CreateTranscriptionWordCommand command);

    List<TranscriptionWordEntity> createAll(@NotNull List<@Valid @NotNull CreateTranscriptionWordCommand> commands);

    TranscriptionWordEntity patch(@Valid @NotNull PatchTranscriptionWordCommand command);

}
