package echoflux.core.core.error;

public class PropagatedException extends RuntimeException {

    public PropagatedException(String message) {
        super(message);
    }

}
