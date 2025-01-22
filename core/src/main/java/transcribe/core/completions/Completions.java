package transcribe.core.completions;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import transcribe.core.core.provider.AiProviderAware;

@Validated
public interface Completions extends AiProviderAware {

    CompletionResult complete(@NotEmpty String input);

}
