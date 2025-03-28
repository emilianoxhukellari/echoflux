package transcribe.core.audio.splitter.temp_file;

import transcribe.core.core.temp_file.TempDirectory;
import transcribe.core.core.utils.TsFiles;

import java.nio.file.Path;

public enum AudioSplitterTempDirectory implements TempDirectory {

    INSTANCE;

    private static final Path TMP_DIR_PATH = TsFiles.newTempDirectory("split");

    @Override
    public Path locationPath() {
        return TMP_DIR_PATH;
    }

}
