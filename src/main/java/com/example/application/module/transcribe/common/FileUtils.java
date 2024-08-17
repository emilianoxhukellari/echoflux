package com.example.application.module.transcribe.common;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtils {

    @SneakyThrows
    public static Path newTempDirectory(String prefix) {
        return Files.createTempDirectory(prefix);
    }

}
