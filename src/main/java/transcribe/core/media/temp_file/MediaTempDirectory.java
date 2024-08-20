package transcribe.core.media.temp_file;

import transcribe.core.common.temp_file.TempDirectory;
import transcribe.core.common.utils.FileUtils;

import java.nio.file.Path;

public enum MediaTempDirectory implements TempDirectory {

    INSTANCE;

    private static final Path TMP_DIR_PATH = FileUtils.newTempDirectory("media");

    @Override
    public Path locationPath() {
        return TMP_DIR_PATH;
    }

}
