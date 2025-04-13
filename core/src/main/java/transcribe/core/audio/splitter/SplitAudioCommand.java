package transcribe.core.audio.splitter;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.core.concurrency.ConcurrencyLevel;
import transcribe.core.core.validate.constraint.duration.PositiveOrZeroDuration;

import java.nio.file.Path;
import java.time.Duration;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SplitAudioCommand {

    @NotNull
    private Path audio;

    @NotNull
    @PositiveOrZeroDuration
    private Duration partitionDuration;

    @NotNull
    @PositiveOrZeroDuration
    private Duration toleranceDuration;

    @NotNull
    @PositiveOrZeroDuration
    private Duration minSilenceDuration;

    @NotNull
    @Builder.Default
    private Integer concurrency = ConcurrencyLevel.AVAILABLE_PROCESSORS;

}
