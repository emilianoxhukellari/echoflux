package transcribe.domain.transcript.transcript_part.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.transcript.transcript_part.data.TranscriptPartEntity;

import java.util.List;

@Validated
public interface TranscriptPartService {

    TranscriptPartEntity getForTranscriptionAndSequence(@NotNull Long transcriptionId, @NotNull Integer sequence);

    List<TranscriptPartEntity> getAllForTranscription(@NotNull Long transcriptionId);

    TranscriptPartEntity create(@Valid @NotNull CreateTranscriptPartCommand command);

}