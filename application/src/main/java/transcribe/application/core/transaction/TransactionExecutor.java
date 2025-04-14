package transcribe.application.core.transaction;

import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.validation.annotation.Validated;

import java.util.function.Consumer;

@Validated
public interface TransactionExecutor {

    <T> T execute(@NotNull TransactionCallback<T> callback);

    <T> T executeReadOnly(@NotNull TransactionCallback<T> callback);

    void executeWithoutResult(@NotNull Consumer<TransactionStatus> callback);

}
