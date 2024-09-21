package transcribe.domain.transcription.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.transcription.data.TranscriptionMetadataEntity;

@Validated
public interface TranscriptionMetadataService {

    TranscriptionMetadataEntity update(@Valid @NotNull UpdateTranscriptionMetadataCommand command);

    double getRealTimeFactor();

}
