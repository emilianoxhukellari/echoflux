package echoflux.core.storage;

import jakarta.validation.constraints.NotNull;

public interface StorageProviderAware {

    @NotNull
    StorageProvider getStorageProvider();

}
