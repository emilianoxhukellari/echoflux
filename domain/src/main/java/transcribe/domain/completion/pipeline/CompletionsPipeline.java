package transcribe.domain.completion.pipeline;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.completion.data.CompletionProjection;

@Validated
public interface CompletionsPipeline {

    CompletionProjection complete(@Valid @NotNull CompleteCommand command);

}
