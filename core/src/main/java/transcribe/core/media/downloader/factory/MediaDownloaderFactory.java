package transcribe.core.media.downloader.factory;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.core.media.downloader.MediaDownloader;

import java.net.URI;
import java.util.Optional;

@Validated
public interface MediaDownloaderFactory {

    Optional<MediaDownloader> findDownloader(@NotNull URI uri);

    default MediaDownloader getRequired(@NotNull URI uri) {
        return findDownloader(uri).orElseThrow(() -> new IllegalArgumentException("No downloader found for " + uri));
    }

}
