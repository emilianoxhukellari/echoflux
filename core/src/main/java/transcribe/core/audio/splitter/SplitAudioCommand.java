package transcribe.core.audio.splitter;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.core.concurrency.ConcurrencyLevel;
import transcribe.core.core.validate.constraint.duration_range.DurationRange;

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
    @DurationRange(minMillis = 0)
    private Duration partitionDuration;

    @NotNull
    @DurationRange(minMillis = 0)
    private Duration toleranceDuration;

    @NotNull
    @DurationRange(minMillis = 0)
    private Duration minSilenceDuration;

    @NotNull
    @Builder.Default
    private Integer concurrency = ConcurrencyLevel.AVAILABLE_PROCESSORS;

}
