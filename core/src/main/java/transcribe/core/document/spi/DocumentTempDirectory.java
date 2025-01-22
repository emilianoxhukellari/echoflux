package transcribe.core.document.spi;

import transcribe.core.core.temp_file.TempDirectory;
import transcribe.core.core.utils.MoreFiles;

import java.nio.file.Path;

public enum DocumentTempDirectory implements TempDirectory {

    INSTANCE;

    private static final Path TMP_DIR_PATH = MoreFiles.newTempDirectory("document");

    @Override
    public Path locationPath() {
        return TMP_DIR_PATH;
    }

}
