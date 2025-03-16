package transcribe.core.core.validate.guard;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.Objects;

public final class Guard {

    private static final String COLLECTION_EMPTY_ERROR = "The collection must be empty";
    private static final String LE_ERROR = "The left value must be less than or equal to the right value";

    public static <T> Collection<T> empty(Collection<T> collection) {
        return empty(collection, COLLECTION_EMPTY_ERROR);
    }

    public static <T> Collection<T> empty(Collection<T> collection, String message, Object... args) {
        if (CollectionUtils.isNotEmpty(collection)) {
            throw new IllegalArgumentException(String.format(message, args));
        }

        return collection;
    }

    public static void le(int left, int right) {
        le(left, right, LE_ERROR);
    }

    public static void le(int left, int right, String message, Object... args) {
        if (left > right) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    public static Class<Enum<?>> enumType(Class<?> beanType) {
        Objects.requireNonNull(beanType, "beanType must not be null");

        if (!beanType.isEnum()) {
            throw new IllegalArgumentException("The bean type must be an enum");
        }

        @SuppressWarnings("unchecked")
        var enumType = (Class<Enum<?>>) beanType;

        return enumType;
    }

}
