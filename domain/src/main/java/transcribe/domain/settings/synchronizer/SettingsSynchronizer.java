package transcribe.domain.settings.synchronizer;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.settings.data.SettingsEntity;

@Validated
public interface SettingsSynchronizer {

    void synchronize();

    SettingsEntity reset(@NotBlank String key);

}
