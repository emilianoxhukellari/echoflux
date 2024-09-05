package transcribe.application.core.error;

import transcribe.core.common.error.PropagatedCoreException;
import transcribe.domain.core.error.PropagatedDomainException;

public final class ErrorUtils {

    public static boolean isPropagated(Throwable throwable) {
        while (throwable != null) {
            if (isInstanceOfPropagated(throwable)) {
                return true;
            }

            throwable = throwable.getCause();
        }

        return false;
    }

    public static boolean isInstanceOfPropagated(Throwable throwable) {
        if (throwable == null) {
            return false;
        }

        return throwable instanceof PropagatedApplicationException
                || throwable instanceof PropagatedCoreException
                || throwable instanceof PropagatedDomainException;
    }

}
