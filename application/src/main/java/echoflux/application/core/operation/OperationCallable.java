package echoflux.application.core.operation;

@FunctionalInterface
public interface OperationCallable<T> {

    T call();

    static <T> OperationCallable<T> ofRunnable(Runnable runnable) {
        return () -> {
            runnable.run();
            return null;
        };
    }

}
