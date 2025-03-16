package transcribe.core.cloud_storage;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.core.validate.constraint.duration_range.DurationRange;

import java.net.URI;
import java.time.Duration;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetSignedUrlOfUriCommand {

    @NotNull
    private URI cloudUri;

    @NotNull
    @DurationRange(minMillis = 0)
    Duration duration;

}
