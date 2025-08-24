package echoflux.application.core.error;

import echoflux.application.core.security.AuthenticatedUser;
import echoflux.domain.core.security.PermissionType;
import echoflux.core.core.error.PropagatedException;

import java.util.Objects;

public final class MoreErrors {

    public static boolean isPropagated(Throwable throwable) {
        while (throwable != null) {
            if (throwable instanceof PropagatedException) {
                return true;
            }

            throwable = throwable.getCause();
        }

        return false;
    }

    public static String resolveErrorMessage(Throwable throwable) {
        Objects.requireNonNull(throwable, "throwable");

        if (AuthenticatedUser.checkPermission(PermissionType.ERROR_VISIBLE)) {
            return throwable.getMessage();
        } else {
            if (isPropagated(throwable)) {
                return throwable.getCause().getMessage();
            } else {
                return "An error occurred. Please try again or contact support.";
            }
        }
    }

}
