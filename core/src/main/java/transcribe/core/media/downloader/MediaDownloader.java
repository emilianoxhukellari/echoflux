package transcribe.core.media.downloader;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.core.core.temp_file.TempFileNameGenerator;
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

    Path download(@NotNull(message = "Video uri is required") URI uri, @NotNull MediaDownloadProgressCallback callback);

    Optional<MediaFindResult> find(@NotNull(message = "Video uri is required") URI uri);

    boolean supports(@NotNull(message = "Video uri is required") URI uri);

    MediaDownloaderProvider provider();

}
