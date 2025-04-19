package echoflux.domain.transcription_word.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import echoflux.domain.transcription_word.data.WordDto;

import java.util.List;

@Validated
public interface TranscriptionWordService {

    List<WordDto> findAllByTranscriptionId(@NotNull Long transcriptionId);

}
