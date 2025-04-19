package echoflux.application.core.operation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.apache.commons.lang3.StringUtils;
import echoflux.core.core.no_op.NoOp;
import echoflux.domain.operation.data.OperationType;

import java.time.Duration;
import java.util.function.Consumer;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@With
public class Operation<T> {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private OperationCallable<T> callable;

    @NotNull
    @Builder.Default
    private Runnable beforeCall = NoOp.runnable();

    @NotNull
    @Builder.Default
    private Consumer<T> onSuccess = NoOp.consumer();

    @NotNull
    @Builder.Default
    private OperationSuccessImportance successImportance = OperationSuccessImportance.NORMAL;

    @NotNull
    @Builder.Default
    private String customSuccessMessage = StringUtils.EMPTY;

    @Builder.Default
    private boolean onSuccessNotify = true;

    @NotNull
    @Builder.Default
    private Consumer<Throwable> onError = NoOp.consumer();

    @NotNull
    @Builder.Default
    private OperationErrorImportance errorImportance = OperationErrorImportance.NORMAL;

    @NotNull
    @Builder.Default
    private String customErrorMessage = StringUtils.EMPTY;

    @Builder.Default
    private boolean onErrorNotify = true;

    @Builder.Default
    private boolean onErrorLog = true;

    @NotNull
    @Builder.Default
    private Runnable onFinally = NoOp.runnable();

    @NotNull
    @Builder.Default
    private OperationType type = OperationType.BLOCKING;

    @Builder.Default
    private boolean onProgressNotify = true;

    @NotNull
    @Builder.Default
    private Duration timeout = Duration.ofHours(4);

}
