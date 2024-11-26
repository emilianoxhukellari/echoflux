package transcribe.core.function;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public final class FunctionUtils {

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

}
