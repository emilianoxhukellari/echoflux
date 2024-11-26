package transcribe.domain.transcript.transcript_part.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.transcript.transcript_part.data.TranscriptPartEntity;

import java.util.List;

@Validated
public interface TranscriptPartService {

    List<TranscriptPartEntity> getAllForTranscription(Long transcriptionId);

    TranscriptPartEntity create(@Valid @NotNull CreateTranscriptPartCommand command);

}