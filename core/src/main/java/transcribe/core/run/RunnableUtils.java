package transcribe.core.run;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public final class RunnableUtils {

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

}
