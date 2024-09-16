package transcribe.core.core.no_op;

import lombok.extern.slf4j.Slf4j;
import transcribe.core.core.progress.ProgressCallback;
import transcribe.core.media.downloader.MediaDownloadProgressCallback;

import java.util.function.Consumer;

@Slf4j
public final class NoOp {

    public static Runnable runnable() {
        return () -> log.debug("No-op runnable");
    }

    public static <T> Consumer<T> consumer() {
        return _ -> log.debug("No-op consumer");
    }

    public static MediaDownloadProgressCallback mediaDownloadProgressCallback() {
        return p -> log.debug("No-op media download progress callback: {}", p);
    }

    public static ProgressCallback progressCallback() {
        return p -> log.debug("No-op progress callback: {}", p);
    }

}
