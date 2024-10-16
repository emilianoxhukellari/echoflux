package transcribe.domain.settings.loader.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import transcribe.core.core.json.JsonMapper;
import transcribe.core.settings.Settings;
import transcribe.core.settings.SettingsLoader;
import transcribe.domain.settings.service.SettingsService;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SettingsLoaderImpl implements SettingsLoader {

    private final SettingsService service;
    private final JsonMapper mapper;

    @Override
    public <T> T load(Class<T> type) {
        var annotation = Objects.requireNonNull(
                AnnotationUtils.findAnnotation(type, Settings.class),
                "Settings annotation not found"
        );
        var key = Validate.notBlank(annotation.key(), "Settings annotation key is blank");
        var entity = service.get(key);

        return mapper.toValue(entity.getValue(), type);
    }

}
