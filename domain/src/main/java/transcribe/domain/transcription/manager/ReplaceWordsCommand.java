package transcribe.domain.transcription.manager;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplaceWordsCommand {

    @NotNull
    private Long transcriptionId;

    @NotNull
    private String words;

    @NotNull
    @Min(0)
    private Integer fromSequence;

    @NotNull
    @Min(0)
    private Integer size;

}
