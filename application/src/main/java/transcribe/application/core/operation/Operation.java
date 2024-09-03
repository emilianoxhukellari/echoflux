package transcribe.application.core.operation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import transcribe.domain.operation.data.OperationType;

import java.util.function.Consumer;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Operation<T> {

    private String name;
    private String description;
    private OperationCallable<T> callable;
    private Consumer<T> onSuccess;
    private Consumer<Throwable> onError;
    private Runnable onFinally;
    private OperationType type = OperationType.BLOCKING;

}
