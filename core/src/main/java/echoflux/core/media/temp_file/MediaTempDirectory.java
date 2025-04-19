package echoflux.core.media.temp_file;

import echoflux.core.core.temp_file.TempDirectory;
import echoflux.core.core.utils.TsFiles;

import java.nio.file.Path;

public enum MediaTempDirectory implements TempDirectory {

    INSTANCE;

    private static final Path TMP_DIR_PATH = TsFiles.newTempDirectory("media");

    @Override
    public Path locationPath() {
        return TMP_DIR_PATH;
    }

}
