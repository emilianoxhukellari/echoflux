package echoflux.core.media.downloader.yt_dlp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YtDlpRequest {

    private File directory;
    private String[] arguments;

}
