package transcribe.domain.settings.service;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSettingsCommand {

    @NotBlank
    private String key;

    @NotBlank
    private String name;

    @NotNull
    private JsonNode value;

}
