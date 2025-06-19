package echoflux.core.core.bean.loader;

import echoflux.core.storage.Storage;
import echoflux.core.storage.StorageProvider;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Validated
public interface BeanLoader {

    <T> Optional<T> findWhen(@NotNull Class<T> beanType, @NotNull Predicate<T> predicate);

    <T> T loadWhen(@NotNull Class<T> beanType, @NotNull Predicate<T> predicate);

    <T> T load(@NotNull Class<T> beanType);

    default Storage loadStorage(@NotNull StorageProvider storageProvider) {
        return loadWhen(Storage.class, s -> Objects.equals(s.getStorageProvider(), storageProvider));
    }

}
