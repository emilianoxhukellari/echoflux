package echoflux.core.core.utils;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import jakarta.annotation.Nullable;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ThreadUtils;
import org.apache.commons.lang3.Validate;
import echoflux.core.core.concurrency.ConcurrencyLevel;
import echoflux.core.core.executor.MoreExecutors;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Slf4j
public final class TsFunctions {

    private final static ExecutorService virtualThreadExecutor = MoreExecutors.virtualThreadExecutor();

    public static Optional<Throwable> runQuietly(Runnable runnable) {
        try {
            runnable.run();
            return Optional.empty();
        } catch (Throwable e) {
            log.error("Error occurred during silent execution: {}", e.getMessage());
            return Optional.of(e);
        }
    }

    public static void runIfPresent(@Nullable Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static <T> void consumeIfPresent(@Nullable Consumer<T> consumer, T value) {
        if (consumer != null) {
            consumer.accept(value);
        }
    }

    public static Duration runTimed(Runnable runnable) {
        Objects.requireNonNull(runnable, "Runnable must not be null");

        var start = System.nanoTime();
        runnable.run();

        return Duration.ofNanos(System.nanoTime() - start);
    }

    public static void runTimed(Runnable runnable, Consumer<Duration> consumer) {
        Objects.requireNonNull(consumer, "Consumer must not be null");

        consumer.accept(runTimed(runnable));
    }

    public static <T> TimedResult<T> getTimed(Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "Supplier must not be null");

        var start = System.nanoTime();
        var result = supplier.get();

        return TimedResult.<T>builder()
                .result(result)
                .duration(Duration.ofNanos(System.nanoTime() - start))
                .build();
    }

    public static void runSynchronized(Runnable runnable, ReentrantLock lock) {
        Objects.requireNonNull(runnable, "Runnable must not be null");
        Objects.requireNonNull(lock, "Lock must not be null");

        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    public static <T> T pollUntil(Supplier<T> supplier, Predicate<T> until, Duration interval, Duration timeout) {
        Objects.requireNonNull(supplier, "Supplier must not be null");
        Objects.requireNonNull(until, "Until predicate must not be null");
        Objects.requireNonNull(interval, "Interval must not be null");
        Objects.requireNonNull(timeout, "Timeout must not be null");

        long startNanos = System.nanoTime();
        long timeoutNanos = timeout.toNanos();

        while (true) {
            var result = supplier.get();
            if (until.test(result)) {
                return result;
            }

            if (System.nanoTime() - startNanos > timeoutNanos) {
                throw new IllegalStateException("Polling timed out");
            }

            ThreadUtils.sleepQuietly(interval);
        }
    }

    public static <T> CompletableFuture<T> getAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, virtualThreadExecutor);
    }

    public static <T> List<T> getAllParallel(List<Supplier<T>> suppliers, int concurrency) {
        return executeAllParallel(suppliers, Supplier::get, concurrency);
    }

    public static <T, R> List<R> executeAllParallel(List<T> items, Function<T, R> function) {
        return executeAllParallel(items, function, ConcurrencyLevel.UNBOUND);
    }

    @SneakyThrows
    public static <T, R> List<R> executeAllParallel(List<T> items, Function<T, R> function, int concurrency) {
        Objects.requireNonNull(items, "Items must not be null");
        Objects.requireNonNull(function, "Function must not be null");
        Validate.isTrue(concurrency == ConcurrencyLevel.UNBOUND || concurrency > 0,
                "Concurrency must be greater than 0 or %d", ConcurrencyLevel.UNBOUND);

        if (items.isEmpty()) {
            return List.of();
        }

        if (items.size() == 1) {
            var singleResult = function.apply(items.getFirst());

            return List.of(singleResult);
        }

        if (concurrency == 1) {
            return executeAll(items, function);
        }

        List<ListenableFuture<R>> futures;
        if (concurrency == ConcurrencyLevel.UNBOUND) {
            futures = items.stream()
                    .map(i -> Futures.submit(() -> function.apply(i), virtualThreadExecutor))
                    .toList();
        } else {
            var semaphore = new Semaphore(concurrency);
            futures = items.stream()
                    .map(i -> Futures.submit(() -> {
                        semaphore.acquire();
                        try {
                            return function.apply(i);
                        } finally {
                            semaphore.release();
                        }
                    }, virtualThreadExecutor))
                    .toList();
        }

        return Futures.allAsList(futures)
                .get();
    }

    public static <T, R> List<R> executeAll(List<T> items, Function<T, R> function) {
        Objects.requireNonNull(items, "Items must not be null");
        Objects.requireNonNull(function, "Function must not be null");

        return items.stream()
                .map(function)
                .toList();
    }

}
