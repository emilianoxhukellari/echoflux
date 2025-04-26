package echoflux.core.cloud_storage;

import com.google.cloud.storage.BlobId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import echoflux.core.core.utils.MoreUris;

import java.net.URI;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceInfo {

    private String resourceName;
    private URI uri;

    public static ResourceInfo ofBlobId(BlobId blobId) {
        return ResourceInfo.builder()
                .resourceName(blobId.getName())
                .uri(MoreUris.newUri(blobId.toGsUtilUri()))
                .build();
    }

}
