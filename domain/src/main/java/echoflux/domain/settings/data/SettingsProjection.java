package echoflux.domain.settings.data;

import com.fasterxml.jackson.databind.JsonNode;
import echoflux.domain.core.data.BaseProjection;
import org.immutables.value.Value;

@Value.Immutable
public interface SettingsProjection extends BaseProjection<Long> {

    String getKey();

    String getName();

    JsonNode getValue();

}
