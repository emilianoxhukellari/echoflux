package echoflux.domain.completion.pipeline;

import echoflux.domain.completion.data.ScalarCompletionProjection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CompletionsPipeline {

    ScalarCompletionProjection complete(@Valid @NotNull CompleteCommand command);

}
