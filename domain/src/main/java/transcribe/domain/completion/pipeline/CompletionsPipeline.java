package transcribe.domain.completion.pipeline;

import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Validated
public interface CompletionsPipeline {

    Optional<CompletionsPipelineResult> complete(String input);

}
