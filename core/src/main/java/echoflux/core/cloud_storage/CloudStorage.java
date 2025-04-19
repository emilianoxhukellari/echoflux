package echoflux.core.cloud_storage;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.net.URL;

@Validated
public interface CloudStorage {

    ResourceInfo upload(@Valid @NotNull CloudUploadCommand command);

    boolean delete(@Valid @NotNull CloudDeleteCommand command);

    URL getSignedUrl(@Valid @NotNull GetSignedUrlOfResourceCommand command);

    URL getSignedUrl(@Valid @NotNull GetSignedUrlOfUriCommand command);

}
