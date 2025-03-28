package transcribe.core.core.utils;

import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class TsLists {

    public static <T> List<T> toSorted(@Nullable List<T> list, Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);

        return ListUtils.emptyIfNull(list)
                .stream()
                .sorted(comparator)
                .toList();
    }

    public static <T, U extends Comparable<? super U>> List<T> toSorted(@Nullable List<T> list,
                                                                        Function<T, U> keyExtractor) {
        Objects.requireNonNull(keyExtractor);

        return toSorted(list, Comparator.comparing(keyExtractor));
    }

    /**
     * @return the last element of the list or null if the list is empty
     */
    @Nullable
    public static <T> T getLastSafe(@Nullable List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        return list.getLast();
    }

    /**
     * @return the first element of the list or null if the list is empty
     */
    @Nullable
    public static <T> T getFirstSafe(@Nullable List<T> list) {
        return getSafe(list, 0);
    }

    /**
     * @return the element at the given index or null if the list is empty or the index is out of bounds
     */
    @Nullable
    public static <T> T getSafe(@Nullable List<T> list, int index) {
        if (CollectionUtils.isEmpty(list) || index < 0 || index >= list.size()) {
            return null;
        }

        return list.get(index);
    }

    /**
     * @return true if the list contains the given element. If the list is null, it is treated as empty
     */
    public static <T> boolean contains(@Nullable List<T> list, T element) {
        return ListUtils.emptyIfNull(list)
                .contains(element);
    }

    /**
     * @return a new list containing the results of applying the given function to each element of the original list
     */
    public static <T, R> List<R> collect(@Nullable List<T> list, Function<T, R> mapper) {
        return ListUtils.emptyIfNull(list)
                .stream()
                .map(mapper)
                .toList();
    }

    public static <T> List<T> add(@Nullable List<T> list, T element) {
        return ListUtils.union(ListUtils.emptyIfNull(list), List.of(element));
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(@Nullable List<T> list) {
        return (T[]) ListUtils.emptyIfNull(list).toArray();
    }

}
