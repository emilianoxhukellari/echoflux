package transcribe.core.settings;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface SettingsLoader {

    <T> @Valid @NotNull T load(@NotNull Class<T> type);

}
