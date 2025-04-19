package echoflux.core.media.downloader.yt_dlp;

public class UncheckedYtDlpException extends RuntimeException {

    public UncheckedYtDlpException(String message) {
        super(message);
    }

    public UncheckedYtDlpException(String message, Throwable cause) {
        super(message, cause);
    }

}
