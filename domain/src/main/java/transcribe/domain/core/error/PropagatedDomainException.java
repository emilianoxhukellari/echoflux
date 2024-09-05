package transcribe.domain.core.error;

public class PropagatedDomainException extends RuntimeException {

    public PropagatedDomainException(String message) {
        super(message);
    }

}
