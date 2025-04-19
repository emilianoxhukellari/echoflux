package echoflux.domain.completion.pipeline;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import echoflux.domain.completion.data.CompletionProjection;

@Validated
public interface CompletionsPipeline {

    CompletionProjection complete(@Valid @NotNull CompleteCommand command);

}
