package transcribe.application.core.operation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import transcribe.core.common.no_op.NoOp;
import transcribe.domain.operation.data.OperationType;

import java.util.function.Consumer;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Operation<T> {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private OperationCallable<T> callable;

    @NotNull
    @Builder.Default
    private Consumer<T> onSuccess = NoOp.consumer();

    @NotNull
    @Builder.Default
    private Consumer<Throwable> onError = NoOp.consumer();

    @NotNull
    @Builder.Default
    private Runnable onFinally = NoOp.runnable();

    @NotNull
    @Builder.Default
    private OperationType type = OperationType.BLOCKING;

}
