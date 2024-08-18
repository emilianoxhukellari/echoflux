package com.example.application.core.audio.transcoder;

import com.example.application.core.audio.common.AudioContainer;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscodeCommand {

    @NotNull
    private Path source;

    @NotNull
    @Builder.Default
    private AudioContainer audioContainer = AudioContainer.OGG;

    @Min(1)
    @Builder.Default
    private int channels = 1;

}
