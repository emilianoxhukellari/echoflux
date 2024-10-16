package transcribe.domain.settings.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.settings.data.SettingsEntity;

import java.util.Collection;
import java.util.List;

@Validated
public interface SettingsService {

    List<SettingsEntity> createAll(@NotNull Collection<@Valid @NotNull CreateSettingsCommand> commandCollection);

    List<SettingsEntity> updateAll(@NotNull Collection<@Valid @NotNull UpdateSettingsCommand> commandCollection);

    SettingsEntity update(@Valid @NotNull UpdateSettingsCommand command);

    void deleteAll(@NotNull Collection<@NotNull Long> idCollection);

    SettingsEntity get(@NotBlank String key);

    List<SettingsEntity> getAllByKeys(@NotNull Collection<@NotBlank String> keys);

    List<SettingsEntity> getAllByKeysExcluding(@NotNull Collection<@NotBlank String> keys);

}
