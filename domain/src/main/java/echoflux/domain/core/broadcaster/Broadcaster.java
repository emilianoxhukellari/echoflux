package echoflux.domain.core.broadcaster;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import echoflux.core.core.utils.MoreFunctions;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Validated
public interface Broadcaster {

    <T> Subscription subscribe(@NotNull Class<T> event, @NotNull Consumer<T> consumer, @NotNull Predicate<T> condition);

    <T> void publish(@Valid @NotNull T event);

    default <T> void publishQuietly(@Valid @NotNull T event) {
        MoreFunctions.runQuietly(() -> publish(event));
    }

}
