package echoflux.domain.settings.synchronizer;

import echoflux.domain.settings.data.SettingsProjection;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public interface SettingsSynchronizer {

    void synchronize();

    SettingsProjection reset(@NotBlank String key);

}
