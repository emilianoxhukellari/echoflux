package transcribe.application.core.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import transcribe.core.audio.transcoder.temp_file.TranscoderTempDirectory;
import transcribe.core.media.temp_file.MediaTempDirectory;

import java.io.File;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
public class TempFileCleanupScheduler {

    private final static List<File> DIRECTORIES = List.of(
            MediaTempDirectory.INSTANCE.locationFile(),
            TranscoderTempDirectory.INSTANCE.locationFile()
    );
    private final static int DELETE_AFTER_MINUTES = 30;

    @Scheduled(fixedDelay = 1000 * 60)
    public void cleanup() {
        var ageFilter = new AgeFileFilter(Instant.now().minusSeconds(DELETE_AFTER_MINUTES * 60));

        DIRECTORIES.stream()
                .filter(File::exists)
                .filter(File::isDirectory)
                .flatMap(d -> FileUtils.listFiles(d, ageFilter, TrueFileFilter.TRUE).stream())
                .peek(FileUtils::deleteQuietly)
                .forEach(f -> log.warn("Deleted old temp file: {}", f));
    }

}
