package transcribe.core.document.spi;

import transcribe.core.core.temp_file.TempDirectory;
import transcribe.core.core.utils.TsFiles;

import java.nio.file.Path;

public enum DocumentTempDirectory implements TempDirectory {

    INSTANCE;

    private static final Path TMP_DIR_PATH = TsFiles.newTempDirectory("document");

    @Override
    public Path locationPath() {
        return TMP_DIR_PATH;
    }

}
