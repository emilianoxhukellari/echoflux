package transcribe.core.core.executor;

import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MoreExecutors {

    private final static ExecutorService delegatingSecurityVirtualThreadExecutor = new DelegatingSecurityContextExecutorService(
            Executors.newVirtualThreadPerTaskExecutor()
    );

    public static ExecutorService virtualThreadExecutor() {
        return delegatingSecurityVirtualThreadExecutor;
    }

}
