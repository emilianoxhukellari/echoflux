package transcribe.core.diarization.pyannote;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Data
@ConfigurationProperties(prefix = "pyannote")
@Validated
public class PyannoteProperties {

    @NotBlank
    private String apiKey;

    @NotBlank
    private String baseUrl;

}
