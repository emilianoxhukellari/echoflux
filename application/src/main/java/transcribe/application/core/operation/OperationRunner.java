package transcribe.application.core.operation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface OperationRunner {

    <T> void run(@Valid @NotNull Operation<T> operation);

}
