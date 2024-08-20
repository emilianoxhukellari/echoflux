package transcribe.core.media.downloader;

@FunctionalInterface
public interface MediaDownloadProgressCallback {

    void onDownloading(int progress);

}
