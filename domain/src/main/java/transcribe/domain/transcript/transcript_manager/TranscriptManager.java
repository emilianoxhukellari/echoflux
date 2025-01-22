package transcribe.domain.transcript.transcript_manager;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.transcript.transcript_part.data.TranscriptPartEntity;
import transcribe.domain.transcript.transcript_part.part.PartModel;

import java.util.List;

@Validated
public interface TranscriptManager {

    List<String> getTranscriptPartitionsWithMetadata(@NotNull Long transcriptionId);

    String getTranscript(@NotNull Long transcriptionId);

    List<PartModel> getTranscriptPartModels(@NotNull Long transcriptionId);

    List<TranscriptPartEntity> saveOriginalParts(@Valid @NotNull SaveOriginalParts command);

    List<TranscriptPartEntity> saveEnhancedParts(@Valid @NotNull SaveEnhancedParts command);

}
