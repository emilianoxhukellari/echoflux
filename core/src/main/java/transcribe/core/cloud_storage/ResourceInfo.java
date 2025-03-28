package transcribe.core.cloud_storage;

import com.google.cloud.storage.BlobId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.core.utils.TsUris;

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
                .uri(TsUris.newUri(blobId.toGsUtilUri()))
                .build();
    }

}
