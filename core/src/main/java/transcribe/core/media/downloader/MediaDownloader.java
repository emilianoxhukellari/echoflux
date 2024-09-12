package transcribe.core.media.downloader;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.core.common.log.LoggedMethodExecution;
import transcribe.core.common.temp_file.TempFileNameGenerator;
import transcribe.core.media.downloader.provider.MediaDownloaderProvider;
import transcribe.core.media.temp_file.MediaTempDirectory;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Downloaded media is saved in {@link MediaTempDirectory}
 **/
@Validated
public interface MediaDownloader extends TempFileNameGenerator {

    @LoggedMethodExecution
    default Path download(@NotNull(message = "Video uri is required") URI uri) {
        return download(uri, _ -> {});
    }

    Path download(@NotNull(message = "Video uri is required") URI uri, @NotNull MediaDownloadProgressCallback callback);

    Optional<MediaFindResult> find(@NotNull(message = "Video uri is required") URI uri);

    boolean supports(@NotNull(message = "Video uri is required") URI uri);

    MediaDownloaderProvider provider();

}
