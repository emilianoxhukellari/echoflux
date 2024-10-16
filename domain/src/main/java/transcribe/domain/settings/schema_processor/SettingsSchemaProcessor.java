package transcribe.domain.settings.schema_processor;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface SettingsSchemaProcessor {

    <T> JsonNode create(@NotNull Class<T> beanType);

    <T> JsonNode adaptToSchema(@NotNull Class<T> beanType, @NotNull JsonNode current);

}
