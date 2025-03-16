package transcribe.core.media.downloader.yt_dlp.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import transcribe.core.media.downloader.yt_dlp.UncheckedYtDlpException;
import transcribe.core.media.downloader.yt_dlp.YtDlp;
import transcribe.core.media.downloader.yt_dlp.YtDlpMediaUnavailableException;
import transcribe.core.media.downloader.yt_dlp.YtDlpRequest;
import transcribe.core.media.downloader.yt_dlp.YtDlpResponse;
import transcribe.core.properties.YtDlpProperties;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class YtDlpImpl implements YtDlp {

    private final YtDlpProperties ytDlpProperties;

    @Override
    public YtDlpResponse execute(YtDlpRequest request) throws YtDlpMediaUnavailableException {
        var commandLine = CommandLine.parse(ytDlpProperties.getExecutionPath())
                .addArguments(request.getArguments(), true);

        try (var outStream = new ByteArrayOutputStream(); var errorStream = new ByteArrayOutputStream()) {
            var executor = DefaultExecutor.builder()
                    .setThreadFactory(Thread.ofVirtual().factory())
                    .setWorkingDirectory(request.getDirectory())
                    .setExecuteStreamHandler(new PumpStreamHandler(outStream, errorStream))
                    .get();

            int exitCode = executor.execute(commandLine);
            var out = outStream.toString(StandardCharsets.UTF_8);
            var err = errorStream.toString(StandardCharsets.UTF_8);

            if (exitCode > 0) {
                if (StringUtils.containsIgnoreCase(err, "Video unavailable")) {
                    throw new YtDlpMediaUnavailableException("Media is unavailable");
                } else {
                    throw new UncheckedYtDlpException("yt-dlp failed with error: " + err);
                }
            }

            return YtDlpResponse.builder()
                    .exitCode(exitCode)
                    .output(out)
                    .error(err)
                    .build();
        } catch (IOException e) {
            throw new UncheckedYtDlpException("Failed to execute yt-dlp", e);
        }

    }

}
