package com.example.application.core.cloud_storage;

import com.example.application.core.common.utils.UriUtils;
import com.google.cloud.storage.BlobId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
                .uri(UriUtils.newUri(blobId.toGsUtilUri()))
                .build();
    }

}
