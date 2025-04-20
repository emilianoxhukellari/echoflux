package echoflux.core.media.downloader.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import echoflux.core.core.log.LoggedMethodExecution;
import echoflux.core.core.temp_file.TempFileNameGenerator;
import echoflux.core.core.utils.EfUris;
import echoflux.core.media.downloader.MediaDownloader;
import echoflux.core.media.downloader.MediaFindResult;
import echoflux.core.media.downloader.yt_dlp.YtDlp;
import echoflux.core.media.downloader.yt_dlp.YtDlpMediaUnavailableException;
import echoflux.core.media.downloader.yt_dlp.YtDlpRequest;
import echoflux.core.media.temp_file.MediaTempDirectory;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MediaDownloaderImpl implements MediaDownloader, TempFileNameGenerator {

    private final YtDlp youTubeDL;

    @Override
    @LoggedMethodExecution
    @SneakyThrows
    public Path download(URI uri) {
        var fileName = newFileName();
        var arguments = ArrayUtils.toArray(
                uri.toString(),
                "--format", "bestaudio",
                "--output", String.format("%s.%%(ext)s", fileName)
        );
        var request = YtDlpRequest.builder()
                .directory(MediaTempDirectory.INSTANCE.locationFile())
                .arguments(arguments)
                .build();

        youTubeDL.execute(request);

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
        var request = YtDlpRequest.builder()
                .arguments(
                        ArrayUtils.toArray(
                                uri.toString(),
                                "--skip-download",
                                "--get-title",
                                "--get-thumbnail",
                                "--encoding", "utf-8"
                        )
                )
                .build();

        String out;
        try {
            out = youTubeDL.execute(request).getOutput();
        } catch (YtDlpMediaUnavailableException e) {
            return Optional.empty();
        }

        var parts = StringUtils.split(out, StringUtils.LF);

        return Optional.of(MediaFindResult.builder()
                .title(ArrayUtils.get(parts, 0, StringUtils.EMPTY))
                .thumbnailUri(EfUris.newUri(ArrayUtils.get(parts, 1, StringUtils.EMPTY)))
                .uri(uri)
                .build());
    }

    @Override
    public String fileNamePrefix() {
        return "download";
    }

}
