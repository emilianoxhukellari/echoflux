package com.example.application.module.transcribe.media.downloader.provider.facebook;

import com.example.application.module.transcribe.media.downloader.MediaDownloadProgressCallback;
import com.example.application.module.transcribe.media.downloader.MediaDownloader;
import com.example.application.module.transcribe.media.downloader.MediaTempDirectory;
import com.example.application.module.wrapper.youtube_dl.YouTubeDL;
import com.example.application.module.wrapper.youtube_dl.YouTubeDLRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;

@Component
@Slf4j
public class FacebookMediaDownloader implements MediaDownloader {

    @Override
    @SneakyThrows
    public Path download(URI uri, MediaDownloadProgressCallback callback) {
        var fileName = newFileName();
        var request = newDownloadRequest(uri, fileName);
        YouTubeDL.execute(request, callback);

        var fileFilter = WildcardFileFilter.builder()
                .setWildcards(new String[]{fileName + ".*"})
                .get();

        return FileUtils.listFiles(MediaTempDirectory.locationAsFile(), fileFilter, null).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("File was not saved successfully"))
                .toPath();
    }

    @Override
    public boolean supports(URI uri) {
        return FacebookUtils.isFacebookUri(uri);
    }

    @Override
    public String fileNamePrefix() {
        return "facebook";
    }

    /**
     * @param fileName the name of the file to save the audio to
     * */
    private static YouTubeDLRequest newDownloadRequest(URI uri, String fileName) {

        return YouTubeDLRequest.builder()
                .uri(uri.toString())
                .directory(MediaTempDirectory.locationAsString())
                .options(List.of("format bestaudio", "output " + fileName + ".%(ext)s"))
                .build();
    }

}
