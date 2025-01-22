package transcribe.domain.transcript.transcript_manager;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaveOriginalParts {

    @NotNull
    private Long transcriptionId;

    @NotNull
    private List<@Valid @NotNull TranscriptPartition> partitions;

}
