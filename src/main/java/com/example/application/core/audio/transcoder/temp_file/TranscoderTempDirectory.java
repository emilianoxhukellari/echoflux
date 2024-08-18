package com.example.application.core.audio.transcoder.temp_file;

import com.example.application.core.common.temp_file.TempDirectory;
import com.example.application.core.common.utils.FileUtils;

import java.nio.file.Path;

public enum TranscoderTempDirectory implements TempDirectory {

    INSTANCE;

    private static final Path TMP_DIR_PATH = FileUtils.newTempDirectory("transcode");

    @Override
    public Path locationPath() {
        return TMP_DIR_PATH;
    }

}
