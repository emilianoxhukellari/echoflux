package transcribe.core.media.downloader;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MediaFindResult {

    private String title;
    private URI thumbnailUri;

}
