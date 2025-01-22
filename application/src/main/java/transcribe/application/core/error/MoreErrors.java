package transcribe.application.core.error;

import transcribe.application.core.spring.SpringContext;
import transcribe.application.security.AuthenticatedUser;
import transcribe.core.core.error.PropagatedException;

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

    public static String resolveErrorMessage(Throwable e) {
        if (SpringContext.getBean(AuthenticatedUser.class).isAdmin()) {
            return e.getMessage();
        } else {
            if (isPropagated(e)) {
                return e.getCause().getMessage();
            } else {
                return "An error occurred. Please try again or contact support.";
            }
        }
    }

}
