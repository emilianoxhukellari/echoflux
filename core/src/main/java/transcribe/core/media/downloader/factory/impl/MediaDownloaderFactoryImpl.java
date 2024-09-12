package transcribe.core.media.downloader.factory.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import transcribe.core.media.downloader.MediaDownloader;
import transcribe.core.media.downloader.factory.MediaDownloaderFactory;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MediaDownloaderFactoryImpl implements MediaDownloaderFactory {

    private final List<MediaDownloader> mediaDownloaderList;

    public Optional<MediaDownloader> findDownloader(URI uri) {
        return mediaDownloaderList.stream()
                .filter(d -> d.supports(uri))
                .findFirst();
    }

}
