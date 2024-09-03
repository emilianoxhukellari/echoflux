package transcribe.domain.common.error;

public class PropagatedDomainException extends RuntimeException {

    public PropagatedDomainException(String message) {
        super(message);
    }

}
