package echoflux.core.core.utils;

public class ParallelExecutionException extends RuntimeException {

    public ParallelExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParallelExecutionException(Throwable cause) {
        super(cause);
    }

}
