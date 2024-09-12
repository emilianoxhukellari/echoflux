package transcribe.core.media.downloader.provider.facebook;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import transcribe.core.common.log.LoggedMethodExecution;
import transcribe.core.common.utils.UriUtils;
import transcribe.core.media.downloader.MediaDownloadProgressCallback;
import transcribe.core.media.downloader.MediaDownloader;
import transcribe.core.media.downloader.MediaFindResult;
import transcribe.core.media.downloader.provider.MediaDownloaderProvider;
import transcribe.core.media.downloader.youtube_dl.YouTubeDL;
import transcribe.core.media.downloader.youtube_dl.YouTubeDLMediaNotFound;
import transcribe.core.media.downloader.youtube_dl.YouTubeDLRequest;
import transcribe.core.media.temp_file.MediaTempDirectory;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FacebookMediaDownloader implements MediaDownloader {

    private final YouTubeDL youTubeDL;

    @Override
    @SneakyThrows
    @LoggedMethodExecution
    public Path download(URI uri, MediaDownloadProgressCallback callback) {
        var fileName = newFileName();
        var request = YouTubeDLRequest.builder()
                .uri(uri.toString())
                .directory(MediaTempDirectory.INSTANCE.locationString())
                .options(List.of("format bestaudio", "output " + fileName + ".%(ext)s"))
                .build();
        youTubeDL.execute(request, callback);

        var fileFilter = WildcardFileFilter.builder()
                .setWildcards(new String[]{fileName + ".*"})
                .get();

        return FileUtils.listFiles(MediaTempDirectory.INSTANCE.locationFile(), fileFilter, null).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("File was not saved successfully"))
                .toPath();
    }

    @Override
    @LoggedMethodExecution
    public Optional<MediaFindResult> find(URI uri) {
        var request = YouTubeDLRequest.builder()
                .uri(uri.toString())
                .options(List.of("skip-download", "get-title", "get-thumbnail"))
                .build();

        String out;
        try {
            out = youTubeDL.execute(request).getOutput();
        } catch (YouTubeDLMediaNotFound e) {
            return Optional.empty();
        }

        var parts = StringUtils.split(out, StringUtils.LF);

        return Optional.of(MediaFindResult.builder()
                .title(ArrayUtils.get(parts, 0, StringUtils.EMPTY))
                .thumbnailUri(UriUtils.newUri(ArrayUtils.get(parts, 1, StringUtils.EMPTY)))
                .build());
    }

    @Override
    public boolean supports(URI uri) {
        return FacebookUtils.isFacebookUri(uri);
    }

    @Override
    public MediaDownloaderProvider provider() {
        return MediaDownloaderProvider.FACEBOOK;
    }

    @Override
    public String fileNamePrefix() {
        return "facebook";
    }

}
