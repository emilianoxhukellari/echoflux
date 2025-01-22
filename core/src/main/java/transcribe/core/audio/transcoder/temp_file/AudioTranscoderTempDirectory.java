package transcribe.core.audio.transcoder.temp_file;

import transcribe.core.core.temp_file.TempDirectory;
import transcribe.core.core.utils.MoreFiles;

import java.nio.file.Path;

public enum AudioTranscoderTempDirectory implements TempDirectory {

    INSTANCE;

    private static final Path TMP_DIR_PATH = MoreFiles.newTempDirectory("transcode");

    @Override
    public Path locationPath() {
        return TMP_DIR_PATH;
    }

}
