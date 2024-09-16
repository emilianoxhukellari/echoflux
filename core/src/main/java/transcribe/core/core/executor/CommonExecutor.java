package transcribe.core.core.executor;

import org.springframework.lang.NonNull;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public final class CommonExecutor implements Executor {

    private static final ExecutorService executor = new DelegatingSecurityContextExecutorService(
            Executors.newVirtualThreadPerTaskExecutor()
    );

    @Override
    public void execute(@NonNull Runnable command) {
        executor.execute(command);
    }

}
