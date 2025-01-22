package transcribe.core.cloud_storage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.core.core.log.LoggedMethodExecution;
import transcribe.core.core.validate.constraint.duration_range.DurationRange;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;

@Validated
public interface CloudStorage {

    @LoggedMethodExecution
    default URL getSignedUrl(@NotBlank String resourceName, @NotNull Duration duration) {
        return getSignedUrl(resourceName, duration, false);
    }

    @LoggedMethodExecution
    default URL getSignedUrlTemp(@NotBlank String resourceName, @NotNull Duration duration) {
        return getSignedUrl(resourceName, duration, true);
    }

    @LoggedMethodExecution
    default URL getSignedUrl(@NotNull URI cloudUri, @NotNull @DurationRange Duration duration) {
        return getSignedUrl(cloudUri, duration, false);
    }

    @LoggedMethodExecution
    default URL getSignedUrlTemp(@NotNull URI cloudUri, @NotNull @DurationRange Duration duration) {
        return getSignedUrl(cloudUri, duration, true);
    }

    @LoggedMethodExecution
    default ResourceInfo upload(@NotNull Path path) {
        return upload(path, false);
    }

    @LoggedMethodExecution
    default ResourceInfo uploadTemp(@NotNull Path path) {
        return upload(path, true);
    }

    @LoggedMethodExecution
    default boolean delete(@NotBlank String resourceName) {
        return delete(resourceName, false);
    }

    @LoggedMethodExecution
    default boolean deleteTemp(@NotBlank String resourceName) {
        return delete(resourceName, true);
    }

    URL getSignedUrl(@NotNull URI cloudUri, @NotNull @DurationRange java.time.Duration duration, boolean temp);

    URL getSignedUrl(@NotNull String resourceName, @NotNull @DurationRange java.time.Duration duration, boolean temp);

    ResourceInfo upload(@NotNull Path path, boolean temp);

    boolean delete(@NotBlank String resourceName, boolean temp);

}
