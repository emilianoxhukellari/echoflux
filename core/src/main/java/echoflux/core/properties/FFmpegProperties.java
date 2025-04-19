package echoflux.core.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Data
@ConfigurationProperties(prefix = "ffmpeg")
@Validated
public class FFmpegProperties {

    @NotBlank
    private String ffmpegExecutionPath;

    @NotBlank
    private String ffprobeExecutionPath;

}
