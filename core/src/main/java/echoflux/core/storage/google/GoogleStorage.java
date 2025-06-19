package echoflux.core.storage.google;

import com.github.f4b6a3.ulid.UlidCreator;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.StorageOptions;
import echoflux.core.storage.StorageProvider;
import echoflux.core.storage.SaveOptions;
import echoflux.core.core.utils.MoreUris;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.Failable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import echoflux.core.storage.Storage;
import echoflux.core.core.log.LoggedMethodExecution;
import echoflux.core.properties.GoogleCloudProperties;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class GoogleStorage implements Storage, DisposableBean {

    private final GoogleCloudProperties googleCloudProperties;
    private final com.google.cloud.storage.Storage storage;

    public GoogleStorage(@Valid GoogleCloudProperties googleCloudProperties) {
        this.googleCloudProperties = googleCloudProperties;
        this.storage = StorageOptions.newBuilder().build().getService();
    }

    @LoggedMethodExecution
    @SneakyThrows({IOException.class})
    @Override
    public URI save(Path path, SaveOptions options) {
        var name = String.format("%s.%s", UlidCreator.getUlid().toString(), PathUtils.getExtension(path));
        var bucketName = resolveBucketName(options.temp());
        var blobId = BlobId.of(bucketName, name);

        var contentType = StringUtils.getIfBlank(
                options.contentType(),
                () -> Failable.get(() -> Files.probeContentType(path))
        );

        var blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType)
                .build();

        storage.createFrom(blobInfo, path);

        return MoreUris.newUri(blobId.toGsUtilUri());
    }

    @LoggedMethodExecution
    @Override
    public boolean delete(URI uri) {
        var blobId = BlobId.fromGsUtilUri(uri.toString());

        return storage.delete(blobId);
    }

    @LoggedMethodExecution
    @Override
    public URL getSignedUrl(URI uri, Duration duration) {
        var blobId = BlobId.fromGsUtilUri(uri.toString());

        return storage.signUrl(
                BlobInfo.newBuilder(blobId).build(),
                duration.toSeconds(),
                TimeUnit.SECONDS,
                com.google.cloud.storage.Storage.SignUrlOption.withV4Signature()
        );
    }

    @LoggedMethodExecution
    @Override
    public void destroy() throws Exception {
        storage.close();
    }

    @Override
    public StorageProvider getStorageProvider() {
        return StorageProvider.GOOGLE;
    }

    private String resolveBucketName(boolean temp) {
        return temp ? googleCloudProperties.getTempBucketName() : googleCloudProperties.getBucketName();
    }

}
