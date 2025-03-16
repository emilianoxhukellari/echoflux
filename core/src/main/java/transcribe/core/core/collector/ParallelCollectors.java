package transcribe.core.core.collector;

import transcribe.core.core.concurrency.ConcurrencyLevel;
import transcribe.core.core.utils.MoreFunctions;
import transcribe.core.core.utils.TimedResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class ParallelCollectors {

    public static <T> Collector<Supplier<T>, ?, List<T>> toList() {
        return toList(ConcurrencyLevel.UNBOUND);
    }

    public static <T> Collector<Supplier<T>, ?, List<T>> toList(int concurrency) {
        return toList(suppliers -> MoreFunctions.getAllParallel(suppliers, concurrency));
    }

    public static <T> Collector<Supplier<T>, ?, TimedResult<List<T>>> toListTimed() {
        return toListTimed(ConcurrencyLevel.UNBOUND);
    }

    public static <T> Collector<Supplier<T>, ?, TimedResult<List<T>>> toListTimed(int concurrency) {
        return toList(suppliers -> MoreFunctions.getTimed(() -> MoreFunctions.getAllParallel(suppliers, concurrency)));
    }

    private static <T, R> Collector<Supplier<T>, ?,R> toList(Function<List<Supplier<T>>, R> finisher) {
        Objects.requireNonNull(finisher, "Finisher must not be null");

        return Collector.of(
                (Supplier<List<Supplier<T>>>) ArrayList::new,
                List::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                finisher
        );
    }

}
