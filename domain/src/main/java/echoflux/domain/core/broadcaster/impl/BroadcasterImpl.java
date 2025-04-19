package echoflux.domain.core.broadcaster.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;
import echoflux.core.core.executor.MoreExecutors;
import echoflux.core.core.utils.TsFunctions;
import echoflux.domain.core.broadcaster.Broadcaster;
import echoflux.domain.core.broadcaster.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
@Slf4j
public class BroadcasterImpl implements Broadcaster {

    private final ExecutorService executor = MoreExecutors.delegatingSecurityVirtualThreadExecutor();
    private final ReentrantLock lock = new ReentrantLock();
    private final Map<Class<?>, List<EventConsumer<?>>> eventSubscribersMap = new HashMap<>();

    public <T> Subscription subscribe(Class<T> event, Consumer<T> consumer, Predicate<T> condition) {
        addSynchronized(event, new EventConsumer<>(consumer, condition));

        return () -> removeSynchronized(event, consumer);
    }

    @SuppressWarnings("unchecked")
    public <T> void publish(T event) {
        lock.lock();
        try {
            for (var c : ListUtils.emptyIfNull(eventSubscribersMap.get(event.getClass()))) {
                TsFunctions.runQuietly(() -> {
                    var casted = (EventConsumer<T>) c;
                    if (casted.condition().test(event)) {
                        executor.execute(() -> casted.consumer().accept(event));
                    }
                }).ifPresent(e -> log.error("Error occurred during event publishing: {}", e.getMessage()));
            }
        } finally {
            lock.unlock();
        }
    }

    private void addSynchronized(Class<?> event, EventConsumer<?> consumer) {
        TsFunctions.runSynchronized(
                () -> eventSubscribersMap.computeIfAbsent(event, _ -> new ArrayList<>()).add(consumer),
                lock
        );
    }

    private void removeSynchronized(Class<?> event, Consumer<?> consumer) {
        TsFunctions.runSynchronized(
                () -> eventSubscribersMap.get(event).removeIf(c -> c.consumer().equals(consumer)),
                lock
        );
    }

    private record EventConsumer<T>(Consumer<T> consumer, Predicate<T> condition) {
    }

}