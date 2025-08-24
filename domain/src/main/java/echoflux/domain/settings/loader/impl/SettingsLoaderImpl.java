package echoflux.domain.settings.loader.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.jooq.DSLContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import echoflux.core.core.json.JsonMapper;
import echoflux.core.settings.Settings;
import echoflux.core.settings.SettingsLoader;

import java.util.Objects;

import static echoflux.domain.jooq.Tables.SETTINGS;

@Component
@RequiredArgsConstructor
public class SettingsLoaderImpl implements SettingsLoader {

    private final DSLContext ctx;

    @Override
    public <T> T load(Class<T> type) {
        var annotation = Objects.requireNonNull(
                AnnotationUtils.findAnnotation(type, Settings.class),
                "Settings annotation not found"
        );
        var key = Validate.notBlank(annotation.key(), "Settings annotation key is blank");
        var node = ctx.select(SETTINGS.VALUE)
                .from(SETTINGS)
                .where(SETTINGS.KEY.eq(key))
                .fetchSingle(SETTINGS.VALUE);

        return JsonMapper.toValue(node, type);
    }

}
