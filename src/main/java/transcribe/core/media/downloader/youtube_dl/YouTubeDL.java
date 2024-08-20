package transcribe.core.media.downloader.youtube_dl;

import transcribe.core.media.downloader.MediaDownloadProgressCallback;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface YouTubeDL {

    YouTubeDLResponse execute(@NotNull YouTubeDLRequest request);

    YouTubeDLResponse execute(@NotNull YouTubeDLRequest request, @Nullable MediaDownloadProgressCallback callback);

}
