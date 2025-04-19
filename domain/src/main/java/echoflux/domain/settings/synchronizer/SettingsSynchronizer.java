package echoflux.domain.settings.synchronizer;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import echoflux.domain.settings.data.SettingsProjection;

@Validated
public interface SettingsSynchronizer {

    void synchronize();

    SettingsProjection reset(@NotBlank String key);

}
