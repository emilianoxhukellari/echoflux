package transcribe.core.media.downloader.provider.youtube;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.model.videos.formats.AudioFormat;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;
import transcribe.core.common.log.LoggedMethodExecution;
import transcribe.core.common.utils.MoreLists;
import transcribe.core.common.utils.UriUtils;
import transcribe.core.media.downloader.MediaDownloadProgressCallback;
import transcribe.core.media.downloader.MediaDownloader;
import transcribe.core.media.downloader.MediaFindResult;
import transcribe.core.media.downloader.provider.MediaDownloaderProvider;
import transcribe.core.media.temp_file.MediaTempDirectory;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;

@Component
public class YouTubeMediaDownloader implements MediaDownloader {

    @Override
    @LoggedMethodExecution
    public Path download(URI uri, MediaDownloadProgressCallback callback) {
        var downloader = new YoutubeDownloader();
        var videoId = YouTubeUtils.uriToVideoId(uri);

        var infoRequest = new RequestVideoInfo(videoId);
        var info = downloader.getVideoInfo(infoRequest).data();
        var audioFormat = Validate.notNull(info, "Video not found for download").bestAudioFormat();

        var downloadRequest = newDownloadRequest(audioFormat, callback);
        var download = downloader.downloadVideoFile(downloadRequest);

        Validate.isTrue(download.ok(), "Failed to download video");
        Validate.notNull(download.data(), "The downloaded file has no data");

        return download.data().toPath();
    }

    @Override
    @LoggedMethodExecution
    public Optional<MediaFindResult> find(URI uri) {
        var downloader = new YoutubeDownloader();
        var videoId = YouTubeUtils.uriToVideoId(uri);
        var infoRequest = new RequestVideoInfo(videoId);

        return Optional.ofNullable(downloader.getVideoInfo(infoRequest).data())
                .map(i -> MediaFindResult.builder()
                        .title(i.details().title())
                        .thumbnailUri(UriUtils.newUri(MoreLists.getLast(i.details().thumbnails())))
                        .build());
    }

    @Override
    public boolean supports(URI uri) {
        return YouTubeUtils.isYouTubeUri(uri);
    }

    @Override
    public MediaDownloaderProvider provider() {
        return MediaDownloaderProvider.YOUTUBE;
    }

    @Override
    public String fileNamePrefix() {
        return "youtube";
    }

    private RequestVideoFileDownload newDownloadRequest(AudioFormat audioFormat, MediaDownloadProgressCallback callback) {

        return new RequestVideoFileDownload(audioFormat)
                .saveTo(MediaTempDirectory.INSTANCE.locationFile())
                .renameTo(newFileName())
                .overwriteIfExists(true)
                .callback(YouTubeUtils.newProgressCallback(callback));
    }

}
