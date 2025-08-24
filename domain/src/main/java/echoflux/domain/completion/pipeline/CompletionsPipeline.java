package echoflux.domain.completion.pipeline;

import echoflux.domain.jooq.tables.pojos.Completion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CompletionsPipeline {

    Completion complete(@Valid @NotNull CompleteCommand command);

}
