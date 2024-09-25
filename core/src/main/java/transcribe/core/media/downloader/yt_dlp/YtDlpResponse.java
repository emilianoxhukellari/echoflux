package transcribe.core.media.downloader.yt_dlp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YtDlpResponse {

    private int exitCode;
    private String output;
    private String error;

}
