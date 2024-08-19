package com.example.application.core.cloud_storage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;

@Validated
public interface CloudStorage {

    /**
     * @return resource name
     * */
    ResourceInfo upload(@NotNull Path path);

    boolean delete(@NotBlank String resourceName);

}
