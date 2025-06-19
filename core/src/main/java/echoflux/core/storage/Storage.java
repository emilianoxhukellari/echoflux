package echoflux.core.storage;

import echoflux.core.core.validate.constraint.duration.PositiveDuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;

@Validated
public interface Storage extends StorageProviderAware {

    URI save(@NotNull Path path, @Valid @NotNull SaveOptions options);

    boolean delete(@NotNull URI uri);

    URL getSignedUrl(@NotNull URI uri, @NotNull @PositiveDuration Duration duration);

}
