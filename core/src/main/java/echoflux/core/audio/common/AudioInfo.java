package echoflux.core.audio.common;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.nio.file.Path;

@Builder
public record AudioInfo(@NotNull Path audio, @NotNull AudioContainer container) {
}
