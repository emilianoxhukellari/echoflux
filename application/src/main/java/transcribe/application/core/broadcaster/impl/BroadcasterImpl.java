package transcribe.application.core.broadcaster.impl;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Component;
import transcribe.application.core.broadcaster.Broadcaster;
import transcribe.application.core.broadcaster.SubscriberId;
import transcribe.application.core.broadcaster.Subscription;
import transcribe.core.common.executor.CommonExecutor;
import transcribe.core.common.utils.MoreLists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class BroadcasterImpl implements Broadcaster {

    private final CommonExecutor commonExecutor;
    private final ReentrantLock lock = new ReentrantLock();
    private final Map<Class<?>, List<EventConsumer<?>>> subscribers = new HashMap<>();

    @Override
    public <T> Subscription subscribe(Class<T> event, Consumer<T> consumer) {
        return subscribeById(event, consumer, null);
    }

    /**
     * Only publishers that use the same id using {@link SubscriberId} will be able to send events to this listener.
     * */
    @Override
    public <T> Subscription subscribeById(Class<T> event, Consumer<T> consumer, @Nullable Object id) {
        addSynchronized(event, new EventConsumer<>(consumer, id));

        return () -> removeSynchronized(event, consumer);
    }

    /**
     * Use {@link SubscriberId} for the id field if the event is intended for listeners that subscribe with an id.
     * If the event does not have an id field, but the message is intended for listeners that subscribe with an id,
     * the message will not be sent to any listeners. Otherwise, it will be sent to all listeners.
     * */
    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Override
    public <T> void publish(T event) {
        var subscriberId = getSubscriberIdFieldValue(event);

        doSynchronized(() ->
                ListUtils.emptyIfNull(subscribers.get(event.getClass()))
                        .stream()
                        .map((EventConsumer.class)::cast)
                        .filter(c -> Objects.equals(c.id(), subscriberId))
                        .forEach(c -> commonExecutor.execute(() -> c.consumer().accept(event)))
        );
    }

    private void addSynchronized(Class<?> event, EventConsumer<?> consumer) {
        doSynchronized(() -> subscribers.computeIfAbsent(event, _ -> new ArrayList<>()).add(consumer));
    }

    private void removeSynchronized(Class<?> event, Consumer<?> consumer) {
        doSynchronized(() -> subscribers.get(event).removeIf(c -> c.consumer().equals(consumer)));
    }

    private void doSynchronized(Runnable action) {
        lock.lock();
        try {
            action.run();
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return the value of the field annotated with {@link SubscriberId} or null if the event does not have such field
     * */
    @SneakyThrows
    @Nullable
    private static Object getSubscriberIdFieldValue(Object event) {
        var idField = MoreLists.getFirst(FieldUtils.getFieldsListWithAnnotation(event.getClass(), SubscriberId.class));

        return idField != null ? FieldUtils.readField(idField, event, true) : null;
    }

    private record EventConsumer<T>(Consumer<T> consumer, Object id) {
    }

}


