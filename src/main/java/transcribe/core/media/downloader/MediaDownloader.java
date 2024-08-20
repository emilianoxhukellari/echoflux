package transcribe.core.media.downloader;


import transcribe.common.log.LogMethodExecution;
import transcribe.core.media.temp_file.MediaTempDirectory;
import transcribe.core.common.temp_file.TempFileNameGenerator;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Downloaded media is saved in {@link MediaTempDirectory}
 **/
@Validated
public interface MediaDownloader extends TempFileNameGenerator {

    @LogMethodExecution
    default Path download(@NotNull(message = "Video uri is required") URI uri) {
        return download(uri, p -> {});
    }

    Path download(@NotNull(message = "Video uri is required") URI uri, @NotNull MediaDownloadProgressCallback callback);

    Optional<MediaFindResult> find(@NotNull(message = "Video uri is required") URI uri);

    boolean supports(@NotNull(message = "Video uri is required") URI uri);

}
