package transcribe.core.cloud_storage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.core.validate.constraint.duration_range.DurationRange;

import java.time.Duration;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetSignedUrlOfResourceCommand {

    @NotBlank
    private String resourceName;

    @NotNull
    @DurationRange(minMillis = 0)
    Duration duration;

    @Builder.Default
    private boolean temp = false;

}
