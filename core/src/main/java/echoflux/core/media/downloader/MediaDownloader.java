package echoflux.core.media.downloader;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;


@Validated
public interface MediaDownloader {

    Path download(@NotNull URI uri);

    Optional<MediaFindResult> find(@NotNull URI uri);

}
