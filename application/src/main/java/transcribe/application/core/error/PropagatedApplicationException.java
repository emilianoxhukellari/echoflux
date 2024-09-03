package transcribe.application.core.error;

public class PropagatedApplicationException extends RuntimeException {

    public PropagatedApplicationException(String message) {
        super(message);
    }

}
