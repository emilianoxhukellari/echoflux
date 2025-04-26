package echoflux.core.core.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@Slf4j
public final class MoreFiles {

    @SneakyThrows
    public static Path newTempDirectory(String prefix) {
        var path = Files.createTempDirectory(prefix);
        log.info("Created temporary directory [{}]", path);

        return path;
    }

    public static void deleteIfExists(Collection<Path> paths) {
        deleteIfExists(CollectionUtils.emptyIfNull(paths).toArray(Path[]::new));
    }

    /**
     * Null paths are ignored. If an {@link IOException} is thrown when deleting a file it is logged and ignored.
     * */
    public static void deleteIfExists(Path... paths) {
        for (var path : paths) {
            try {
                if (path != null) {
                    var deleted = Files.deleteIfExists(path);
                    if (deleted) {
                        log.debug("Deleted file: [{}]", path);
                    } else {
                        log.warn("File not found to delete: [{}]", path);
                    }
                }
            } catch (IOException e) {
                log.error("Failed to delete file: [{}]; Error: {}", path, e.getMessage());
            }
        }
    }

}
