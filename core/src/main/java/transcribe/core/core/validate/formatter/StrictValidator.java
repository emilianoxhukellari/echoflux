package transcribe.core.core.validate.formatter;

public interface StrictValidator {

    <T> T validate(T object);

}
