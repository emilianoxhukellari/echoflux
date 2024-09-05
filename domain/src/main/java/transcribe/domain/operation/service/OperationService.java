package transcribe.domain.operation.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.operation.data.OperationEntity;
import transcribe.domain.operation.data.OperationType;

@Validated
public interface OperationService {

    OperationEntity createRunning(@NotBlank String name, String description, @NotNull OperationType type);

    OperationEntity updateSuccess(@NotNull Long id);

    OperationEntity updateFailure(@NotNull Long id, @NotNull Throwable error);

}
