package com.example.application.core.media.downloader.youtube_dl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YouTubeDLRequest {

    private static String DD = "--";

    private String directory;
    private String uri;
    private List<String> options;

    public String buildOptions() {
        var sb = new StringBuilder();

        if (StringUtils.isNotBlank(uri)) {
            sb.append(uri).append(StringUtils.SPACE);
        }
        ListUtils.emptyIfNull(options)
                .forEach(o -> sb.append(DD).append(o).append(StringUtils.SPACE));

        return StringUtils.trim(sb.toString());
    }

}
