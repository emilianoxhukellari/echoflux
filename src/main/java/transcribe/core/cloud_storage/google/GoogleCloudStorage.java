package transcribe.core.cloud_storage.google;

import transcribe.common.log.LoggedMethodExecution;
import transcribe.config.properties.GoogleCloudProperties;
import transcribe.core.cloud_storage.CloudStorage;
import transcribe.core.cloud_storage.ResourceInfo;
import com.github.f4b6a3.ulid.UlidCreator;
import com.google.api.services.storage.StorageScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import jakarta.validation.Valid;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Slf4j
public class GoogleCloudStorage implements CloudStorage, DisposableBean {

    private final GoogleCloudProperties googleCloudProperties;
    private final Storage googleStorage;

    public GoogleCloudStorage(@Valid GoogleCloudProperties googleCloudProperties) {
        this.googleCloudProperties = googleCloudProperties;
        this.googleStorage = newGoogleStorage(googleCloudProperties);
    }

    @Override
    @SneakyThrows
    @LoggedMethodExecution
    public ResourceInfo upload(Path path) {
        var blobId = BlobId.of(googleCloudProperties.getBucketName(), UlidCreator.getUlid().toString());
        var blobInfo = BlobInfo.newBuilder(blobId).build();

        try (var is = Files.newInputStream(path); var writer = googleStorage.writer(blobInfo)) {
            var buffer = new byte[4096];
            int length;
            while ((length = is.read(buffer)) >= 0) {
                writer.write(ByteBuffer.wrap(buffer, 0, length));
            }
        }

        return ResourceInfo.ofBlobId(blobId);
    }

    @Override
    @LoggedMethodExecution
    public boolean delete(String resourceName) {
        return googleStorage.delete(BlobId.of(googleCloudProperties.getBucketName(), resourceName));
    }

    @Override
    public void destroy() throws Exception {
        googleStorage.close();
    }

    @SneakyThrows
    private static Storage newGoogleStorage(GoogleCloudProperties properties) {
        @Cleanup
        var privateKeyStream = IOUtils.toInputStream(properties.getPrivateKey(), StandardCharsets.UTF_8);
        var credentials = GoogleCredentials.fromStream(privateKeyStream).createScoped(StorageScopes.all());

        var storageSettings = StorageOptions.newBuilder()
                .setProjectId(properties.getProjectId())
                .setCredentials(credentials)
                .build();

        return storageSettings.getService();
    }

}
