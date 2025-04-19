package echoflux.core.cloud_storage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import echoflux.core.core.validate.constraint.duration.PositiveOrZeroDuration;

import java.time.Duration;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetSignedUrlOfResourceCommand {

    @NotBlank
    private String resourceName;

    @NotNull
    @PositiveOrZeroDuration
    Duration duration;

    @Builder.Default
    private boolean temp = false;

}
