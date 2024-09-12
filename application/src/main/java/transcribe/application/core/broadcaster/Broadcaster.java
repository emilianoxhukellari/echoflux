package transcribe.application.core.broadcaster;

import jakarta.annotation.Nullable;

import java.util.function.Consumer;

public interface Broadcaster {

    <T> Subscription subscribe(Class<T> event, Consumer<T> consumer);

    <T> Subscription subscribeById(Class<T> event, Consumer<T> consumer, @Nullable Object id);

    <T> void publish(T event);

}
