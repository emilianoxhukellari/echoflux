package com.example.application.module.transcribe.media.downloader;

import com.example.application.module.transcribe.common.FileUtils;

import java.io.File;
import java.nio.file.Path;

public final class MediaTempDirectory {

    private static final Path TMP_DIR_PATH;
    private static final String TMP_DIR_STRING;
    private static final File TMP_DIR_FILE;

    public static Path locationAsPath() {
        return TMP_DIR_PATH;
    }

    public static String locationAsString() {
        return TMP_DIR_STRING;
    }

    public static File locationAsFile() {
        return TMP_DIR_FILE;
    }

    static {
        TMP_DIR_PATH = FileUtils.newTempDirectory("media");
        TMP_DIR_STRING = TMP_DIR_PATH.toString();
        TMP_DIR_FILE = TMP_DIR_PATH.toFile();
    }

}
