package transcribe.core.completions;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

@Validated
public interface Completions {

    CompletionResult complete(@NotEmpty String input);

}
