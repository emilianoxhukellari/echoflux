package transcribe.domain.core.broadcaster.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.stereotype.Component;
import transcribe.core.core.qualifier.Qualifiers;
import transcribe.core.function.FunctionUtils;
import transcribe.domain.core.broadcaster.Broadcaster;
import transcribe.domain.core.broadcaster.Subscription;

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

    @Qualifier(Qualifiers.DELEGATING_SECURITY_VIRTUAL_THREAD_EXECUTOR)
    private final DelegatingSecurityContextExecutorService executor;
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
                FunctionUtils.runQuietly(() -> {
                    var casted = (EventConsumer<T>) c;
                    if (casted.condition().test(event)) {
                        executor.execute(() -> casted.consumer().accept(event));
                    }
                });
            }
        } finally {
            lock.unlock();
        }
    }

    private void addSynchronized(Class<?> event, EventConsumer<?> consumer) {
        FunctionUtils.runSynchronized(
                () -> eventSubscribersMap.computeIfAbsent(event, _ -> new ArrayList<>()).add(consumer),
                lock
        );
    }

    private void removeSynchronized(Class<?> event, Consumer<?> consumer) {
        FunctionUtils.runSynchronized(
                () -> eventSubscribersMap.get(event).removeIf(c -> c.consumer().equals(consumer)),
                lock
        );
    }

    private record EventConsumer<T>(Consumer<T> consumer, Predicate<T> condition) {
    }

}