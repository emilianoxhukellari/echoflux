package com.example.application.core.media.temp_file;

import com.github.f4b6a3.ulid.UlidCreator;

public interface MediaTempFileNameGenerator {

    default String newFileName() {
        return String.format("%s-%s", fileNamePrefix(), UlidCreator.getUlid());
    }

    String fileNamePrefix();

}
