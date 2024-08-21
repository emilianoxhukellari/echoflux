package transcribe.core.media.downloader.provider.youtube;

import com.github.kiulian.downloader.downloader.YoutubeProgressCallback;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import transcribe.core.media.downloader.MediaDownloadProgressCallback;

import java.io.File;
import java.net.URI;
import java.util.regex.Pattern;

public final class YouTubeUtils {

    private static final Pattern PATTERN;

    public static String uriToVideoId(URI uri) {
        Validate.notNull(uri, "URI is required to extract video id");

        var matcher = PATTERN.matcher(uri.toString());
        if (matcher.find()) {
            return matcher.group(1);
        }

        throw new IllegalArgumentException("Invalid YouTube URL");
    }

    public static boolean isYouTubeUri(URI uri) {
        Validate.notNull(uri, "URI is required to check if it is a YouTube URL");

        return StringUtils.contains(uri.toString(), "youtube.");
    }

    public static YoutubeProgressCallback<File> newProgressCallback(MediaDownloadProgressCallback callback) {
        Validate.notNull(callback, "Media download callback is required");

        return new YoutubeProgressCallback<>() {

            @Override
            public void onDownloading(int progress) {
                callback.onDownloading(progress);
            }

            @Override
            public void onFinished(File data) {
            }

            @Override
            public void onError(Throwable throwable) {
            }

        };
    }

    static {
        PATTERN = Pattern.compile("https?://(?:m.)?(?:www\\.)?youtu(?:\\.be/|(?:be-nocookie|be)\\.com/(?:watch|\\w+\\?(?:feature=\\w+.\\w+&)?v=|v/|e/|embed/|live/|shorts/|user/(?:[\\w#]+/)+))([^&#?\\n]+)");
    }

}
