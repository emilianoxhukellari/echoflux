package echoflux.core.cloud_storage.google;

import com.github.f4b6a3.ulid.UlidCreator;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.Failable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import echoflux.core.cloud_storage.CloudDeleteCommand;
import echoflux.core.cloud_storage.CloudStorage;
import echoflux.core.cloud_storage.CloudUploadCommand;
import echoflux.core.cloud_storage.GetSignedUrlOfResourceCommand;
import echoflux.core.cloud_storage.GetSignedUrlOfUriCommand;
import echoflux.core.cloud_storage.ResourceInfo;
import echoflux.core.core.log.LoggedMethodExecution;
import echoflux.core.properties.GoogleCloudProperties;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class GoogleCloudStorage implements CloudStorage, DisposableBean {

    private final GoogleCloudProperties googleCloudProperties;
    private final Storage storage;

    public GoogleCloudStorage(@Valid GoogleCloudProperties googleCloudProperties) {
        this.googleCloudProperties = googleCloudProperties;
        this.storage = StorageOptions.newBuilder().build().getService();
    }

    @LoggedMethodExecution
    @SneakyThrows({IOException.class})
    @Override
    public ResourceInfo upload(CloudUploadCommand command) {
        var name = String.format("%s.%s", UlidCreator.getUlid().toString(), PathUtils.getExtension(command.getPath()));
        var bucketName = resolveBucketName(command.isTemp());
        var blobId = BlobId.of(bucketName, name);

        var contentType = StringUtils.getIfBlank(
                command.getContentType(),
                () -> Failable.get(() -> Files.probeContentType(command.getPath()))
        );

        var blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType)
                .build();

        storage.createFrom(blobInfo, command.getPath());

        return ResourceInfo.ofBlobId(blobId);
    }

    @LoggedMethodExecution
    @Override
    public boolean delete(CloudDeleteCommand command) {
        var bucketName = resolveBucketName(command.isTemp());
        var blobId = BlobId.of(bucketName, command.getResourceName());

        return storage.delete(blobId);
    }

    @LoggedMethodExecution
    @Override
    public URL getSignedUrl(GetSignedUrlOfResourceCommand command) {
        var bucketName = resolveBucketName(command.isTemp());
        var blobId = BlobId.of(bucketName, command.getResourceName());

        return getSignedUrl(blobId, command.getDuration());
    }

    @LoggedMethodExecution
    @Override
    public URL getSignedUrl(GetSignedUrlOfUriCommand command) {
        var blobId = BlobId.fromGsUtilUri(command.getCloudUri().toString());

        return getSignedUrl(blobId, command.getDuration());
    }

    @LoggedMethodExecution
    @Override
    public void destroy() throws Exception {
        storage.close();
    }

    private URL getSignedUrl(BlobId blobId, Duration duration) {
        Objects.requireNonNull(blobId, "blobId cannot be null");
        Objects.requireNonNull(duration, "duration cannot be null");

        return storage.signUrl(
                BlobInfo.newBuilder(blobId).build(),
                duration.toSeconds(),
                TimeUnit.SECONDS,
                Storage.SignUrlOption.withV4Signature()
        );
    }

    private String resolveBucketName(boolean temp) {
        return temp ? googleCloudProperties.getTempBucketName() : googleCloudProperties.getBucketName();
    }

}
