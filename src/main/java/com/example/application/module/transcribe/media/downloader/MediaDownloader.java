package com.example.application.module.transcribe.media.downloader;


import com.example.application.module.transcribe.media.temp_file.MediaTempFileNameGenerator;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.nio.file.Path;

/**
 * Downloaded media is saved in {@link MediaTempDirectory}
 **/
@Validated
public interface MediaDownloader extends MediaTempFileNameGenerator {

    default Path download(@NotNull(message = "Video uri is required") URI uri) {
        return download(uri, p -> {});
    }

    Path download(@NotNull(message = "Video uri is required") URI uri, @NotNull MediaDownloadProgressCallback callback);

    boolean supports(URI uri);

}
