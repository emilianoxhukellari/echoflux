package echoflux.application.core.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import echoflux.core.audio.splitter.temp_file.AudioSplitterTempDirectory;
import echoflux.core.audio.transcoder.temp_file.AudioTranscoderTempDirectory;
import echoflux.core.document.spi.DocumentTempDirectory;
import echoflux.core.media.temp_file.MediaTempDirectory;
import echoflux.core.settings.SettingsLoader;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TempFileCleanupScheduler {

    private final static List<File> DIRECTORIES = List.of(
            MediaTempDirectory.INSTANCE.locationFile(),
            AudioTranscoderTempDirectory.INSTANCE.locationFile(),
            AudioSplitterTempDirectory.INSTANCE.locationFile(),
            DocumentTempDirectory.INSTANCE.locationFile()
    );

    private final SettingsLoader settingsLoader;

    @Scheduled(fixedDelay = 1000 * 60)
    public void cleanup() {
        // todo: test
        var deleteAfterMinutes = settingsLoader.load(TempFileCleanupSettings.class)
                .getDeleteAfterMinutes();
        var ageFilter = new AgeFileFilter(Instant.now().minus(Duration.ofMinutes(deleteAfterMinutes)));

        DIRECTORIES.stream()
                .filter(File::exists)
                .filter(File::isDirectory)
                .flatMap(d -> FileUtils.listFiles(d, ageFilter, TrueFileFilter.TRUE).stream())
                .forEach(f -> {
                    FileUtils.deleteQuietly(f);
                    log.warn("Deleted old temp file: {}", f);
                });
    }

}
