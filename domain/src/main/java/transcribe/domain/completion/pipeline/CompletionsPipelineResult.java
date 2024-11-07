package transcribe.domain.completion.pipeline;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompletionsPipelineResult {

    @NotNull
    private Long completionId;

}
