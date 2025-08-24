package echoflux.domain.settings.endpoint;

import echoflux.domain.core.security.PermissionType;
import echoflux.domain.core.security.RequiredPermissions;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public interface SettingsEndpoint {

    @RequiredPermissions(PermissionType.SETTINGS_SYNCHRONIZE)
    void synchronizeAll();

    @RequiredPermissions(PermissionType.SETTINGS_RESET)
    Long reset(@NotBlank String key);

}
