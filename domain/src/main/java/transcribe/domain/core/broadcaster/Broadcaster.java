package transcribe.domain.core.broadcaster;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.core.run.RunnableUtils;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Validated
public interface Broadcaster {

    <T> Subscription subscribe(@NotNull Class<T> event, @NotNull Consumer<T> consumer, @NotNull Predicate<T> condition);

    default <T> Subscription subscribe(@NotNull Class<T> event, @NotNull Consumer<T> consumer) {
        return subscribe(event, consumer, _ -> true);
    }

    <T> void publish(@Valid @NotNull T event);

    default <T> void publishQuietly(@Valid @NotNull T event) {
        RunnableUtils.runQuietly(() -> publish(event));
    }

}
