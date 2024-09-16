package transcribe.core.core.utils;

import jakarta.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

public final class MoreArrays {

    /**
     * @return a new list containing the elements of the array. If the array is null, an empty list is returned
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(@Nullable T[] array) {
        return (List<T>) Arrays.asList(ArrayUtils.nullToEmpty(array));
    }

}
