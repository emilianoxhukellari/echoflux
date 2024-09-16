package transcribe.application.core.error;

import transcribe.core.core.error.PropagatedException;

public final class ErrorUtils {

    public static boolean isPropagated(Throwable throwable) {
        while (throwable != null) {
            if (throwable instanceof PropagatedException) {
                return true;
            }

            throwable = throwable.getCause();
        }

        return false;
    }

}
