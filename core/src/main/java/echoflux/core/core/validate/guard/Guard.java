package echoflux.core.core.validate.guard;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public final class Guard {

    private static final String COLLECTION_EMPTY_ERROR = "The collection must be empty";
    private static final String COLLECTION_NOT_EMPTY_ERROR = "The collection must not be empty";
    private static final String COLLECTION_SINGLE_OR_EMPTY_ERROR = "The collection must contain a single element or be empty";
    private static final String COLLECTION_SINGLE_ERROR = "The collection must contain a single element";
    private static final String ARRAY_SINGLE_ELEMENT_ERROR = "The array must contain a single element";
    private static final String LE_ERROR = "The left value must be less than or equal to the right value";
    private static final String NULL_ERROR = "The object must not be null";
    private static final String BLANK_ERROR = "The character sequence must not be blank";
    private static final String ASSIGNABLE_FROM_ERROR = "Super type class is not assignable from sub type class";

    public static void assignableFrom(Class<?> superType, Class<?> subType, String message, Object... args) {
        Guard.notNull(superType);

        if (!superType.isAssignableFrom(subType)) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    public static void assignableFrom(Class<?> superType, Class<?> subType) {
        assignableFrom(superType, subType, ASSIGNABLE_FROM_ERROR);
    }

    public static <T extends CharSequence> T notBlank(T chars) {
        return notBlank(chars, BLANK_ERROR);
    }

    public static <T extends CharSequence> T notBlank(T chars, String message, Object... args) {
        return Validate.notBlank(chars, message, args);
    }

    public static <T> T notNull(T object) {
        return notNull(object, NULL_ERROR);
    }

    public static <T> T notNull(T object, String message, Object... args) {
        return Objects.requireNonNull(object, String.format(message, args));
    }

    public static <T> T notNullElse(T object, T defaultValue) {
        return (object != null) ? object : notNull(defaultValue, "defaultValue");
    }

    public static <T> Collection<T> notEmpty(Collection<T> collection) {
        return notEmpty(collection, COLLECTION_NOT_EMPTY_ERROR);
    }

    public static <T> Collection <T> notEmpty(Collection<T> collection, String message, Object... args) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new IllegalArgumentException(String.format(message, args));
        }

        return collection;
    }

    public static <T> Collection<T> empty(Collection<T> collection) {
        return empty(collection, COLLECTION_EMPTY_ERROR);
    }

    public static <T> Collection<T> empty(Collection<T> collection, String message, Object... args) {
        if (CollectionUtils.isNotEmpty(collection)) {
            throw new IllegalArgumentException(String.format(message, args));
        }

        return collection;
    }

    public static <T> Optional<T> singleOrEmpty(Collection<T> collection) {
        return singleOrEmpty(collection, COLLECTION_SINGLE_OR_EMPTY_ERROR);
    }

    public static <T> T single(Collection<T> collection) {
        return single(collection, COLLECTION_SINGLE_ERROR);
    }

    public static <T> Optional<T> singleOrEmpty(Collection<T> collection, String message, Object... args) {
        if (CollectionUtils.size(collection) > 1) {
            throw new IllegalArgumentException(String.format(message, args));
        }

        return collection.isEmpty()
                ? Optional.empty()
                : Optional.of(collection.iterator().next());
    }

    public static <T> T single(Collection<T> collection, String message, Object... args) {
        if (CollectionUtils.size(collection) != 1) {
            throw new IllegalArgumentException(String.format(message, args));
        }

        return collection.iterator().next();
    }

    public static <T> T[] single(T[] array) {
        return single(array, ARRAY_SINGLE_ELEMENT_ERROR);
    }

    public static <T> T[] single(T[] array, String message, Object... args) {
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
