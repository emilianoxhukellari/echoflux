package transcribe.domain.transcript.transcript_manager;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.domain.transcript.transcript_part.part.PartModel;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaveEnhancedParts {

    @NotNull
    private Long transcriptionId;

    @NotNull
    private List<@Valid @NotNull PartModel> partModels;

}
