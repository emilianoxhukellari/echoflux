package com.example.application.core.media.downloader.provider.youtube;

import com.example.application.core.media.downloader.MediaDownloadProgressCallback;
import com.example.application.core.media.downloader.MediaDownloader;
import com.example.application.core.media.temp_file.MediaTempDirectory;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.model.videos.formats.AudioFormat;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.file.Path;

@Component
public class YouTubeMediaDownloader implements MediaDownloader {

    @Override
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
    public boolean supports(URI uri) {
        return YouTubeUtils.isYouTubeUri(uri);
    }

    @Override
    public String fileNamePrefix() {
        return "youtube";
    }

    private RequestVideoFileDownload newDownloadRequest(AudioFormat audioFormat, MediaDownloadProgressCallback callback) {

        return new RequestVideoFileDownload(audioFormat)
                .saveTo(MediaTempDirectory.locationAsFile())
                .renameTo(newFileName())
                .overwriteIfExists(true)
                .callback(YouTubeUtils.newProgressCallback(callback));
    }

}
