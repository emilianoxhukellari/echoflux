package transcribe.core.common.utils;

import org.apache.commons.lang3.Validate;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class RunnableUtils {

    private static final ExecutorService VIRTUAL_THREAD_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    public static Future<?> runOnVirtual(Runnable runnable) {
        Validate.notNull(runnable, "Runnable is required to run on a virtual thread");

        return VIRTUAL_THREAD_EXECUTOR.submit(runnable);
    }

    public static <T> Future<T> runOnVirtual(Callable<T> callable) {
        Validate.notNull(callable, "Callable is required to run on a virtual thread");

        return VIRTUAL_THREAD_EXECUTOR.submit(callable);
    }

    public static <T> void consumeIfPresent(Consumer<T> consumer, T value) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public static void runIf(boolean condition, Runnable runnable) {
        if (condition) {
            runnable.run();
        }
    }

    public static void runIfElse(boolean condition, Runnable ifTrue, Runnable ifFalse) {
        if (condition) {
            ifTrue.run();
        } else {
            ifFalse.run();
        }
    }

}
