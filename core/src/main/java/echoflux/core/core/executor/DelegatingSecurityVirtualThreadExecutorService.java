package echoflux.core.core.executor;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DelegatingSecurityVirtualThreadExecutorService implements ExecutorService {

    private final ExecutorService delegate = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @NotNull
    @Override
    public List<Runnable> shutdownNow() {
        return delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
    }

    @NotNull
    @Override
    public <T> Future<T> submit(@NotNull Callable<T> task) {
        var wrapped = wrap(task);

        return delegate.submit(wrapped);
    }

    @NotNull
    @Override
    public <T> Future<T> submit(@NotNull Runnable task, T result) {
        var wrapped = wrap(task);

        return delegate.submit(wrapped, result);
    }

    @NotNull
    @Override
    public Future<?> submit(@NotNull Runnable task) {
        var wrapped = wrap(task);

        return delegate.submit(wrapped);
    }

    @NotNull
    @Override
    public <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException {
        var wrapped = wrapAll(tasks);

        return delegate.invokeAll(wrapped);
    }

    @NotNull
    @Override
    public <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks,
                                         long timeout,
                                         @NotNull TimeUnit unit) throws InterruptedException {
        var wrapped = wrapAll(tasks);

        return delegate.invokeAll(wrapped, timeout, unit);
    }

    @NotNull
    @Override
    public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        var wrapped = wrapAll(tasks);

        return delegate.invokeAny(wrapped);
    }

    @Override
    public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks,
                           long timeout,
                           @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        var wrapped = wrapAll(tasks);

        return delegate.invokeAny(wrapped, timeout, unit);
    }

    @Override
    public void execute(@NotNull Runnable command) {
        var wrapped = wrap(command);
        delegate.execute(wrapped);
    }

    private Runnable wrap(Runnable delegate) {
        return new DelegatingSecurityContextRunnable(delegate);
    }

    private <T> Callable <T> wrap(Callable<T> delegate) {
        return new DelegatingSecurityContextCallable<>(delegate);
    }

    private <T> Collection<Callable<T>> wrapAll(Collection<? extends Callable<T>> delegates) {
        if (delegates == null) {
            return null;
        }

        return delegates.stream()
                .map(this::wrap)
                .toList();
    }

}
