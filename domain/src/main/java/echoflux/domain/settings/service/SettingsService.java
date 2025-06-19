package echoflux.domain.settings.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import echoflux.domain.settings.data.SettingsEntity;

import java.util.Collection;
import java.util.List;

@Validated
public interface SettingsService {

    SettingsEntity getByKey(@NotBlank String key);

    List<SettingsEntity> getAllByKeys(@NotNull Collection<@NotBlank String> keys);

    List<SettingsEntity> getAllByKeysExcluding(@NotNull Collection<@NotBlank String> keys);

    List<SettingsEntity> createAll(@NotNull Collection<@Valid @NotNull CreateSettingsCommand> commandCollection);

    void deleteAll(@NotNull Collection<@NotNull @Valid SettingsEntity> settings);

}
