package com.example.application.core.transcribe;

import com.example.application.core.transcribe.common.Language;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.List;

@Validated
public interface SpeechToText {

    String transcribe(@NotNull URI cloudUri, @NotEmpty List<@NotNull Language> languages);

    default String transcribe(@NotNull URI cloudUri, @NotNull Language language) {
        return transcribe(cloudUri, List.of(language));
    }

}
