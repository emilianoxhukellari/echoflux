package transcribe.domain.transcript.transcript_manager;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.transcript.transcript_part.data.TranscriptPartEntity;

import java.util.List;

@Validated
public interface TranscriptManager {

    String getTranscriptWithTimestamps(Long transcriptionId);

    List<TranscriptPartEntity> saveAsParts(@Valid @NotNull SaveAsPartsCommand command);

}
