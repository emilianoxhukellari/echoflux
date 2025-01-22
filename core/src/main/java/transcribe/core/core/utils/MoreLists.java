package transcribe.core.core.utils;

import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.function.Function;

public final class MoreLists {

    /**
     * @return the last element of the list or null if the list is empty
     */
    @Nullable
    public static <T> T getLast(@Nullable List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        return list.getLast();
    }

    /**
     * @return the first element of the list or null if the list is empty
     */
    @Nullable
    public static <T> T getFirst(@Nullable List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        return list.getFirst();
    }

    /**
     * @return true if the list contains the given element. If the list is null, it is treated as empty
     */
    public static <T> boolean contains(@Nullable List<T> list, T element) {
        return ListUtils.emptyIfNull(list).contains(element);
    }

    /**
     * @return a new list containing the results of applying the given function to each element of the original list
     */
    public static <T, R> List<R> collect(@Nullable List<T> list, Function<T, R> mapper) {
        return ListUtils.emptyIfNull(list).stream()
                .map(mapper)
                .toList();
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(@Nullable List<T> list) {
        return (T[]) ListUtils.emptyIfNull(list).toArray();
    }

}
