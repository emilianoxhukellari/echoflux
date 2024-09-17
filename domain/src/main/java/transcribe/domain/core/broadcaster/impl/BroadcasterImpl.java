package transcribe.domain.core.broadcaster.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;
import transcribe.domain.core.broadcaster.Broadcaster;
import transcribe.domain.core.broadcaster.Subscription;
import transcribe.core.core.executor.CommonExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class BroadcasterImpl implements Broadcaster {

    private final CommonExecutor commonExecutor;
    private final ReentrantLock lock = new ReentrantLock();
    private final Map<Class<?>, List<EventConsumer<?>>> subscribers = new HashMap<>();

    public <T> Subscription subscribe(Class<T> event, Consumer<T> consumer, Predicate<T> condition) {
        addSynchronized(event, new EventConsumer<>(consumer, condition));

        return () -> removeSynchronized(event, consumer);
    }

    @SuppressWarnings("unchecked")
    public <T> void publish(T event) {
        lock.lock();
        try {
            for (var c : ListUtils.emptyIfNull(subscribers.get(event.getClass()))) {
                try {
                    var casted = (EventConsumer<T>) c;
                    if (casted.condition().test(event)) {
                        commonExecutor.execute(() -> casted.consumer().accept(event));
                    }
                } catch (Throwable _) {
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void addSynchronized(Class<?> event, EventConsumer<?> consumer) {
        lock.lock();
        try {
            subscribers.computeIfAbsent(event, _ -> new ArrayList<>()).add(consumer);
        } finally {
            lock.unlock();
        }
    }

    private void removeSynchronized(Class<?> event, Consumer<?> consumer) {
        lock.lock();
        try {
            subscribers.get(event).removeIf(c -> c.consumer().equals(consumer));
        } finally {
            lock.unlock();
        }
    }

    private record EventConsumer<T>(Consumer<T> consumer, Predicate<T> condition) {
    }

}