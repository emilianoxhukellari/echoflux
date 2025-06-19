package echoflux.application.core.operation;

import echoflux.core.core.validate.guard.Guard;

@FunctionalInterface
public interface OperationCallable<T> {

    T call();

    static <T> OperationCallable<T> ofRunnable(Runnable runnable) {
        Guard.notNull(runnable, "runnable");

        return () -> {
            runnable.run();
            return null;
        };
    }

}
