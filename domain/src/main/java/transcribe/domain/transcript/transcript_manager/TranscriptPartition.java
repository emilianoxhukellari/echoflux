package transcribe.domain.transcript.transcript_manager;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.transcribe.common.SpeechToTextWord;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptPartition {

    @NotNull
    private List<@Valid @NotNull SpeechToTextWord> speechToTextWords;

}
