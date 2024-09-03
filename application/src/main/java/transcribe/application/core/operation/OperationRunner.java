package transcribe.application.core.operation;

public interface OperationRunner {

    <T> void run(Operation<T> operation);

}
