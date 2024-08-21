package transcribe.core.common.utils;

import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.function.Function;

public final class MoreLists {

    @Nullable
    public static <T> T getLast(@Nullable List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        return list.getLast();
    }

    @Nullable
    public static <T> T getFirst(@Nullable List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        return list.getFirst();
    }

    public static <T, R> List<R> collect(@Nullable List<T> list, Function<T, R> mapper) {
        return ListUtils.emptyIfNull(list).stream()
                .map(mapper)
                .toList();
    }

}
