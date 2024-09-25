package transcribe.core.media.downloader.yt_dlp;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface YtDlp {

    YtDlpResponse execute(@Valid @NotNull YtDlpRequest request) throws YtDlpMediaUnavailableException;

}
