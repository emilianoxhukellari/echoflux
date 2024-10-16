package transcribe.core.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Data
@ConfigurationProperties(prefix = "gcloud")
@Validated
public class GoogleCloudProperties {

    @NotBlank
    private String projectId;
    @NotBlank
    private String privateKey;
    @NotBlank
    private String bucketName;
    @NotBlank
    private String speechEndpoint;
    @NotBlank
    private String aiPlatformEndpoint;
    @NotBlank
    private String location;

}
