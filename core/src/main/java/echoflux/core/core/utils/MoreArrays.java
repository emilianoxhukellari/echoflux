package echoflux.core.core.utils;

import jakarta.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class MoreArrays {

    /**
     * @return a new list containing the elements of the array. If the array is null, an empty list is returned
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(@Nullable T[] array) {
        return (List<T>) Arrays.asList(ArrayUtils.nullToEmpty(array));
    }

    @SuppressWarnings("unchecked")
    public static <T, R> R[] collect(@Nullable T[] array, Function<T, R> mapper) {
        if (array == null) {
            return (R[]) ArrayUtils.EMPTY_OBJECT_ARRAY;
        }

        return Arrays.stream(array)
                .map(mapper)
                .toArray(size -> (R[]) Array.newInstance(array.getClass().getComponentType(), size));
    }

    public static <T> T getFirst(@Nullable T[] array) {
        return Optional.ofNullable(array)
                .filter(a -> a.length > 0)
                .map(a -> a[0])
                .orElse(null);
    }

}
