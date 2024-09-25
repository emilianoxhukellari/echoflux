package transcribe.core.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Data
@ConfigurationProperties(prefix = "yt-dlp")
@Validated
public class YtDlpProperties {

    @NotBlank
    private String executionPath;

}
