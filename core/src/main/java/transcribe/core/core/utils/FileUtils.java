package transcribe.core.core.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public final class FileUtils {

    @SneakyThrows
    public static Path newTempDirectory(String prefix) {
        var path = Files.createTempDirectory(prefix);
        log.info("Created temporary directory [{}]", path);

        return path;
    }

    /**
     * Null paths are ignored. If an {@link IOException} is thrown when deleting a file it is logged and ignored.
     * */
    public static void deleteIfExists(Path... paths) {
        for (var path : paths) {
            try {
                if (path != null) {
                    Files.deleteIfExists(path);
                }
            } catch (IOException e) {
                log.error("Failed to delete file: {}; Error: {}", path, e.getMessage());
            }
        }
    }

}
