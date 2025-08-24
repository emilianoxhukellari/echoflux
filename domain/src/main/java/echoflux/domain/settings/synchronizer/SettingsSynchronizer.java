package echoflux.domain.settings.synchronizer;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public interface SettingsSynchronizer {

    void synchronizeAll();

    Long reset(@NotBlank String key);

}
