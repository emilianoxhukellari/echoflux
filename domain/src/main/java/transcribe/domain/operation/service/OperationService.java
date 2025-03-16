package transcribe.domain.operation.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.operation.data.OperationProjection;
import transcribe.domain.operation.data.OperationType;

@Validated
public interface OperationService {

    OperationProjection createRunning(@NotBlank String name, String description, @NotNull OperationType type);

    OperationProjection updateSuccess(@NotNull Long id);

    OperationProjection updateFailure(@NotNull Long id, @NotNull Throwable error);

}
