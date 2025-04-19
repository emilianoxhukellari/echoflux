package echoflux.application.core.transaction.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import echoflux.application.core.transaction.TransactionExecutor;

import java.util.function.Consumer;

@Component
public class TransactionExecutorImpl implements TransactionExecutor {

    private final TransactionTemplate transactionTemplate;
    private final TransactionTemplate readOnlyTransactionTemplate;

    public TransactionExecutorImpl(PlatformTransactionManager platformTransactionManager) {
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
        this.readOnlyTransactionTemplate = new TransactionTemplate(platformTransactionManager);
        this.readOnlyTransactionTemplate.setReadOnly(true);
    }

    @Override
    public <T> T execute(TransactionCallback<T> callback) {
        return transactionTemplate.execute(callback);
    }

    @Override
    public void executeWithoutResult(Consumer<TransactionStatus> callback) {
        transactionTemplate.executeWithoutResult(callback);
    }

    @Override
    public <T> T executeReadOnly(TransactionCallback<T> callback) {
        return readOnlyTransactionTemplate.execute(callback);
    }

}
