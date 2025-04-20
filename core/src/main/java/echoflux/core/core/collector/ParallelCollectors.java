package echoflux.core.core.collector;

import echoflux.core.core.concurrency.ConcurrencyLevel;
import echoflux.core.core.utils.EfFunctions;
import echoflux.core.core.utils.TimedResult;

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
        return toList(suppliers -> EfFunctions.getAllParallel(suppliers, concurrency));
    }

    public static <T> Collector<Supplier<T>, ?, TimedResult<List<T>>> toListTimed() {
        return toListTimed(ConcurrencyLevel.UNBOUND);
    }

    public static <T> Collector<Supplier<T>, ?, TimedResult<List<T>>> toListTimed(int concurrency) {
        return toList(suppliers -> EfFunctions.getTimed(() -> EfFunctions.getAllParallel(suppliers, concurrency)));
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
