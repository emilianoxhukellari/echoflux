package transcribe.core.media.downloader;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.core.core.temp_file.TempFileNameGenerator;
import transcribe.core.media.temp_file.MediaTempDirectory;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Downloaded media is saved in {@link MediaTempDirectory}
 **/
@Validated
public interface MediaDownloader extends TempFileNameGenerator {


    Path download(@NotNull URI uri);

    Optional<MediaFindResult> find(@NotNull URI uri);

}
