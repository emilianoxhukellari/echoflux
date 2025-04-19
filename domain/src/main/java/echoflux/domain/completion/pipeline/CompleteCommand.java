package echoflux.domain.completion.pipeline;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import echoflux.core.core.provider.AiProvider;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompleteCommand {

    @NotBlank
    private String input;

    @NotNull
    private Long transcriptionId;

    private AiProvider aiProvider;

}
