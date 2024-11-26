package transcribe.domain.transcript.transcript_manager;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import transcribe.core.transcribe.common.Word;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaveAsPartsCommand {

    @NotNull
    private Long transcriptionId;

    @NotNull
    private List<@Valid @NotNull Word> words;

}
