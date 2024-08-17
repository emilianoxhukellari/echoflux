package com.example.application.module.wrapper.youtube_dl;

import com.example.application.module.transcribe.media.downloader.MediaDownloadProgressCallback;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

@Slf4j
public final class YouTubeDL {

    private static final String EXECUTABLE_PATH = "youtube-dl";
    private static final Pattern PROGRESS_PATTERN = Pattern.compile("(\\d+\\.\\d+|\\d+)%");

    @SneakyThrows
    public static YouTubeDLResponse execute(YouTubeDLRequest request, MediaDownloadProgressCallback callback) {
        var command = String.format("%s %s", EXECUTABLE_PATH, request.buildOptions());

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

        var stdOutProcessor = Thread.ofVirtual().start(() -> extract(outStream, outBuffer, callback));
        var stdErrProcessor = Thread.ofVirtual().start(() -> copy(errStream, errBuffer));

        stdOutProcessor.join();
        stdErrProcessor.join();
        var exitCode = process.waitFor();

        var out = outBuffer.toString();
        var err = errBuffer.toString();
        if (exitCode > 0) {
            throw new RuntimeException(String.format("Error in YouTubeDL execution: %s", err));
        } else {
            return YouTubeDLResponse.builder()
                    .exitCode(exitCode)
                    .output(out)
                    .error(err)
                    .build();
        }
    }

    private static void copy(InputStream stream, StringBuilderWriter writer) {
        try {
            IOUtils.copy(stream, writer, Charset.defaultCharset());
        } catch (IOException e) {
            log.error("Error copying stream", e);
        }
    }

    @SneakyThrows
    private static void extract(InputStream stream, StringBuilderWriter writer, MediaDownloadProgressCallback callback) {
        var currentLine = new StringBuilder();
        int nextChar;
        while ((nextChar = stream.read()) != -1) {
            writer.write(nextChar);
            if (nextChar == 13 && callback != null) {
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
