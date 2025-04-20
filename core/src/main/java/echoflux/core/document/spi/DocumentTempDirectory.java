package echoflux.core.document.spi;

import echoflux.core.core.temp_file.TempDirectory;
import echoflux.core.core.utils.EfFiles;

import java.nio.file.Path;

public enum DocumentTempDirectory implements TempDirectory {

    INSTANCE;

    private static final Path TMP_DIR_PATH = EfFiles.newTempDirectory("document");

    @Override
    public Path locationPath() {
        return TMP_DIR_PATH;
    }

}
