package echoflux.core.core.bean.accessor;

import echoflux.core.storage.Storage;
import echoflux.core.storage.StorageProvider;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Validated
public interface BeanAccessor {

    <T> Optional<T> findWhen(@NotNull Class<T> beanType, @NotNull Predicate<T> predicate);

    <T> T getWhen(@NotNull Class<T> beanType, @NotNull Predicate<T> predicate);

    <T> T get(@NotNull Class<T> beanType);

    default Storage getStorage(@NotNull StorageProvider storageProvider) {
        return getWhen(Storage.class, s -> Objects.equals(s.getStorageProvider(), storageProvider));
    }

}
