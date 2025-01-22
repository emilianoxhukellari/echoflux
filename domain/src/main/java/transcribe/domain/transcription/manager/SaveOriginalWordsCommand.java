package transcribe.domain.transcription.manager;

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
public class SaveOriginalWordsCommand {

    @NotNull
    private Long transcriptionId;

    @NotNull
    private List<@Valid @NotNull SpeechToTextWord> words;

}
