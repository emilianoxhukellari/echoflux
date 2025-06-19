package echoflux.application.core.operation;

import echoflux.core.core.validate.guard.Guard;
import lombok.Builder;
import lombok.With;
import org.apache.commons.lang3.StringUtils;
import echoflux.core.core.no_op.NoOp;

import java.time.Duration;
import java.util.function.Consumer;

@Builder
@With
public record Operation<T>(String name,
                           OperationCallable<T> callable,
                           Runnable beforeCall,
                           Consumer<T> onSuccess,
                           OperationSuccessImportance successImportance,
                           String customSuccessMessage,
                           Boolean onSuccessNotify,
                           Consumer<Throwable> onError,
                           OperationErrorImportance errorImportance,
                           String customErrorMessage,
                           Boolean onErrorNotify,
                           Boolean onErrorLog,
                           Runnable onFinally,
                           OperationType type,
                           Boolean onProgressNotify,
                           Duration timeout) {

    public Operation {
        Guard.notNull(callable, "callable");

        beforeCall = Guard.notNullElse(beforeCall, NoOp.runnable());
        onSuccess = Guard.notNullElse(onSuccess, NoOp.consumer());
        successImportance = Guard.notNullElse(successImportance, OperationSuccessImportance.NORMAL);
        customSuccessMessage = Guard.notNullElse(customSuccessMessage, StringUtils.EMPTY);
        onSuccessNotify = Guard.notNullElse(onSuccessNotify, true);
        onError = Guard.notNullElse(onError, NoOp.consumer());
        errorImportance = Guard.notNullElse(errorImportance, OperationErrorImportance.NORMAL);
        customErrorMessage = Guard.notNullElse(customErrorMessage, StringUtils.EMPTY);
        onErrorNotify = Guard.notNullElse(onErrorNotify, true);
        onErrorLog = Guard.notNullElse(onErrorLog, true);
        onFinally = Guard.notNullElse(onFinally, NoOp.runnable());
        type = Guard.notNullElse(type, OperationType.BLOCKING);
        onProgressNotify = Guard.notNullElse(onProgressNotify, true);
        timeout = Guard.notNullElse(timeout, Duration.ofHours(1));
    }

    /**
     * <p>
     *     Runs this operation using the {@link OperationRunner}.
     * </p>
     * */
    public void run() {
        OperationRunner.run(this);
    }

    /**
     * <p>
     *     Runs this operation in background mode. If this operation type is not configured to be background,
     *     it will be converted to a background operation and then executed.
     * </p>
     * */
    public void runBackground() {
        if (OperationType.BACKGROUND == this.type) {
            OperationRunner.run(this);
        } else {
            var backgroundOperation = this.withType(OperationType.BACKGROUND);
            OperationRunner.run(backgroundOperation);
        }
    }

}
