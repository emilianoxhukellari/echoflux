package transcribe.core.media.downloader.youtube_dl.impl;

import transcribe.config.properties.YouTubeDLProperties;
import transcribe.core.common.utils.RunnableUtils;
import transcribe.core.media.downloader.MediaDownloadProgressCallback;
import jakarta.annotation.Nullable;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import transcribe.core.media.downloader.youtube_dl.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Slf4j
@Component
public class YouTubeDLImpl implements YouTubeDL {

    private static final Pattern PROGRESS_PATTERN = Pattern.compile("(\\d+\\.\\d+|\\d+)%");
    private static final int CARRIAGE_RETURN = 13;

    private final YouTubeDLProperties youTubeDLProperties;

    public YouTubeDLImpl(YouTubeDLProperties youTubeDLProperties) {
        this.youTubeDLProperties = youTubeDLProperties;
    }

    /**
     * @throws YouTubeDLMediaNotFound if the media was not found
     * @throws YouTubeDLException if an error occurred during the execution
     * */
    @Override
    public YouTubeDLResponse execute(YouTubeDLRequest request) {
        return execute(request, null);
    }

    /**
     * @throws YouTubeDLMediaNotFound if the media was not found
     * @throws YouTubeDLException if an error occurred during the execution
     * */
    @Override
    @SneakyThrows
    public YouTubeDLResponse execute(YouTubeDLRequest request, MediaDownloadProgressCallback callback) {
        var command = String.format("%s %s", youTubeDLProperties.getExecutionPath(), request.buildOptions());

        var processBuilder = new ProcessBuilder(StringUtils.split(command, StringUtils.SPACE));
        if (StringUtils.isNotBlank(request.getDirectory())) {
            processBuilder.directory(new File(request.getDirectory()));
        }

        var process = processBuilder.start();
        @Cleanup
        var outStream = process.getInputStream();
        @Cleanup
        var errStream = process.getErrorStream();
        @Cleanup
        var outBuffer = new StringBuilderWriter();
        @Cleanup
        var errBuffer = new StringBuilderWriter();

        var stdOutProcessor = RunnableUtils.runOnVirtual(() -> extract(outStream, outBuffer, callback));
        var stdErrProcessor = RunnableUtils.runOnVirtual(() -> extract(errStream, errBuffer));

        stdOutProcessor.get();
        stdErrProcessor.get();
        var exitCode = process.waitFor();

        var out = outBuffer.toString();
        var err = errBuffer.toString();
        if (exitCode > 0) {
            if (StringUtils.containsAny(err, "404", "Unsupported URL")) {
                throw new YouTubeDLMediaNotFound(err);
            } else {
                throw new YouTubeDLException(String.format("Error in YouTubeDLImpl execution: %s", err));
            }
        } else {
            return YouTubeDLResponse.builder()
                    .exitCode(exitCode)
                    .output(out)
                    .error(err)
                    .build();
        }
    }

    private static void extract(InputStream stream, StringBuilderWriter writer) {
        try {
            IOUtils.copy(stream, writer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error copying stream", e);
        }
    }

    @SneakyThrows
    private static void extract(InputStream stream, StringBuilderWriter writer, @Nullable MediaDownloadProgressCallback callback) {
        var currentLine = new StringBuilder();
        int nextChar;
        while ((nextChar = stream.read()) != -1) {
            writer.write(nextChar);
            if (nextChar == CARRIAGE_RETURN && callback != null) {
                var matcher = PROGRESS_PATTERN.matcher(currentLine);
                if (matcher.find()) {
                    callback.onDownloading((int) Float.parseFloat(matcher.group(1)));
                }
                currentLine.setLength(0);
            } else if(callback != null) {
                currentLine.append((char) nextChar);
            }
        }
    }

}
