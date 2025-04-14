package transcribe.core.core.validate.guard;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.Objects;

public final class Guard {

    private static final String COLLECTION_EMPTY_ERROR = "The collection must be empty";
    private static final String COLLECTION_SINGLE_ELEMENT_ERROR = "The collection must contain a single element";
    private static final String ARRAY_SINGLE_ELEMENT_ERROR = "The array must contain a single element";
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

    public static <T> Collection<T> singleElement(Collection<T> collection) {
        return singleElement(collection, COLLECTION_SINGLE_ELEMENT_ERROR);
    }

    public static <T> Collection<T> singleElement(Collection<T> collection, String message, Object... args) {
        if (CollectionUtils.size(collection) != 1) {
            throw new IllegalArgumentException(String.format(message, args));
        }

        return collection;
    }

    public static <T> T[] singleElement(T[] array) {
        return singleElement(array, ARRAY_SINGLE_ELEMENT_ERROR);
    }

    public static <T> T[] singleElement(T[] array, String message, Object... args) {
        if (CollectionUtils.size(array) != 1) {
            throw new IllegalArgumentException(String.format(message, args));
        }

        return array;
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
