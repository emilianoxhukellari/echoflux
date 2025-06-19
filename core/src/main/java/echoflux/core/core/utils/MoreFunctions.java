package echoflux.core.core.utils;

import echoflux.core.core.executor.DelegatingSecurityVirtualThreadExecutorService;
import echoflux.core.core.supplier.MoreSuppliers;
import echoflux.core.core.tuple.Tuple2;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ThreadUtils;
import org.apache.commons.lang3.Validate;
import echoflux.core.core.concurrency.ConcurrencyLevel;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Slf4j
public final class MoreFunctions {

    private final static ExecutorService EXECUTOR_SERVICE = new DelegatingSecurityVirtualThreadExecutorService();

    /**
     * <p>
     *     Runs the given runnable and returns an {@link Optional} containing any exception that occurred during execution.
     *     This method never throws an exception.
     * </p>
     * */
    public static Optional<Throwable> runQuietly(Runnable runnable) {
        try {
            runnable.run();
            return Optional.empty();
        } catch (Throwable e) {
            log.error("Error occurred during silent execution: {}", e.getMessage());
            return Optional.of(e);
        }
    }

    /**
     * Runs the given runnable if it is not null.
     * */
    public static void runIfPresent(@Nullable Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }

    /**
     * Runs the given consumer with the provided value if the consumer is not null.
     * */
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

    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, EXECUTOR_SERVICE);
    }

    /**
     * <p>
     * Executes multiple runnables in parallel, aborting any further execution if any of the runnables fails.
     * </p>
     * <p>
     * {@link ConcurrencyLevel#UNBOUND} is used as concurrency level, meaning all runnables will be executed in parallel at once.
     * </p>
     *
     * @throws ParallelExecutionException if any of the runnables fails or if the execution is interrupted.
     */
    public static void runAllParallelAbortOnFailure(Runnable... runnables) {
        Objects.requireNonNull(runnables, "Runnables must not be null");

        var suppliers = Arrays.stream(runnables)
                .map(MoreSuppliers::ofRunnable)
                .toList();

        executeAllParallelAbortOnFailure(suppliers, ConcurrencyLevel.UNBOUND);
    }

    /**
     * <p>
     * Executes multiple suppliers in parallel, aborting any further execution if any of the suppliers fails.
     * </p>
     *
     * @param suppliers   the list of suppliers to execute
     * @param concurrency the maximum number of items to process in parallel;
     *                    use {@link ConcurrencyLevel#UNBOUND} for unbounded concurrency
     *                    and {@link ConcurrencyLevel#AVAILABLE_PROCESSORS} for a bounded concurrency based on available processors.
     */
    public static <T> List<T> executeAllParallelAbortOnFailure(List<Supplier<T>> suppliers, int concurrency) {
        return executeAllParallelAbortOnFailure(suppliers, Supplier::get, concurrency);
    }

    /**
     * <p>
     * Executes two suppliers in parallel, aborting any further execution if any of the suppliers fails.
     * </p>
     *
     * @return {@link Tuple2} containing the results of the two suppliers.
     * @throws ParallelExecutionException if any of the suppliers fails or if the execution is interrupted.
     */
    @SuppressWarnings("preview")
    public static <T1, T2> Tuple2<T1, T2> executeAllParallelAbortOnFailure(Supplier<T1> first, Supplier<T2> second) {
        Objects.requireNonNull(first, "First supplier must not be null");
        Objects.requireNonNull(second, "Second supplier must not be null");

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var r1 = scope.fork(first::get);
            var r2 = scope.fork(second::get);

            try {
                scope.join().throwIfFailed(ParallelExecutionException::new);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ParallelExecutionException("Execution interrupted", e);
            }

            return Tuple2.of(r1.get(), r2.get());
        }
    }

    /**
     * <p>
     * Applies a function to each item in the list in parallel, aborting any further execution if any of the function applications fails.
     * </p>
     *
     * @param items       the list of items to process.
     * @param function    the function to apply to each item in the list.
     * @param concurrency the maximum number of items to process in parallel;
     *                    use {@link ConcurrencyLevel#UNBOUND} for unbounded concurrency
     *                    and {@link ConcurrencyLevel#AVAILABLE_PROCESSORS} for a bounded concurrency based on available processors.
     * @return a list of results from applying the function to each item in the input list.
     * @throws ParallelExecutionException if any of the function applications fails or if the execution is interrupted.
     */
    @SuppressWarnings("preview")
    public static <T, R> List<R> executeAllParallelAbortOnFailure(List<T> items, Function<T, R> function, int concurrency) {
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

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var subtasks = new ArrayList<StructuredTaskScope.Subtask<R>>(items.size());

            if (concurrency == ConcurrencyLevel.UNBOUND) {
                for (var item : items) {
                    var subtask = scope.fork(() -> function.apply(item));
                    subtasks.add(subtask);
                }
            } else {
                var semaphore = new Semaphore(concurrency);
                for (var item : items) {
                    var subtask = scope.fork(() -> {
                        try {
                            semaphore.acquire();
                            return function.apply(item);
                        } finally {
                            semaphore.release();
                        }
                    });
                    subtasks.add(subtask);
                }
            }

            try {
                scope.join().throwIfFailed(ParallelExecutionException::new);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ParallelExecutionException("Execution interrupted", e);
            }

            return subtasks.stream()
                    .map(StructuredTaskScope.Subtask::get)
                    .toList();
        }
    }

    public static <T, R> List<R> executeAll(List<T> items, Function<T, R> function) {
        Objects.requireNonNull(items, "Items must not be null");
        Objects.requireNonNull(function, "Function must not be null");

        return items.stream()
                .map(function)
                .toList();
    }

}
