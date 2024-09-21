package transcribe.core.media.downloader;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.core.core.log.LoggedMethodExecution;
import transcribe.core.core.no_op.NoOp;
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

    @LoggedMethodExecution
    default Path download(@NotNull URI uri) {
        return download(uri, NoOp.mediaDownloadProgressCallback());
    }

    Path download(@NotNull URI uri, @NotNull MediaDownloadProgressCallback callback);

    Optional<MediaFindResult> find(@NotNull URI uri);

    boolean supports(@NotNull URI uri);

    MediaDownloaderProvider provider();

}
