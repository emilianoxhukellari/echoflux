package transcribe.core.core.bean.loader;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.function.Predicate;

@Validated
public interface BeanLoader {

    <T> Optional<T> findWhen(@NotNull Class<T> beanType, @NotNull Predicate<T> predicate);

    <T> T loadWhen(@NotNull Class<T> beanType, @NotNull Predicate<T> predicate);

}
