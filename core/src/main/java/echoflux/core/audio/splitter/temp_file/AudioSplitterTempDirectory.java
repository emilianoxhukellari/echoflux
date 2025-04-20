package echoflux.core.audio.splitter.temp_file;

import echoflux.core.core.temp_file.TempDirectory;
import echoflux.core.core.utils.EfFiles;

import java.nio.file.Path;

public enum AudioSplitterTempDirectory implements TempDirectory {

    INSTANCE;

    private static final Path TMP_DIR_PATH = EfFiles.newTempDirectory("split");

    @Override
    public Path locationPath() {
        return TMP_DIR_PATH;
    }

}
