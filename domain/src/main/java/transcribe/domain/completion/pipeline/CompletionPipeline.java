package transcribe.domain.completion.pipeline;

import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Validated
public interface CompletionPipeline {

    Optional<CompletionPipelineResult> complete(String input);

}
