package transcribe.domain.settings.data;

import com.fasterxml.jackson.databind.JsonNode;

public record SettingsProjection(Long id,
                                 String key,
                                 String name,
                                 JsonNode value) {
}
