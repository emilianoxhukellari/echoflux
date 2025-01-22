package transcribe.domain.transcription.manager;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.transcription_word.data.TranscriptionWordEntity;

import java.util.List;

@Validated
public interface TranscriptionManager {

    List<TranscriptionWordEntity> saveOriginalWords(@Valid @NotNull SaveOriginalWordsCommand command);

}
