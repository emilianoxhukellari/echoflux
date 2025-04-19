package echoflux.core.core.error;

import org.apache.commons.lang3.ArrayUtils;

public final class PropagatedAssert {

    public static void isTrue(boolean condition, String message, Object... values) {
        if (!condition) {
            throw new PropagatedException(getMessage(message, values));
        }
    }

    private static String getMessage(String message, Object... values) {
        return ArrayUtils.isEmpty(values) ? message : String.format(message, values);
    }

}
