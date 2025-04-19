package echoflux.core.core.executor;

import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MoreExecutors {

    private final static ExecutorService DELEGATING_SECURITY_VIRTUAL_THREAD_EXECUTOR = new DelegatingSecurityContextExecutorService(
            Executors.newVirtualThreadPerTaskExecutor()
    );
    private final static ExecutorService VIRTUAL_THREAD_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    public static ExecutorService delegatingSecurityVirtualThreadExecutor() {
        return DELEGATING_SECURITY_VIRTUAL_THREAD_EXECUTOR;
    }

    public static ExecutorService virtualThreadExecutor() {
        return VIRTUAL_THREAD_EXECUTOR;
    }

}
