package echoflux.core.core.utils;

import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import echoflux.core.core.bean.MoreBeans;
import echoflux.core.core.display_name.DisplayName;

public final class EfEnums {

    public static <T extends Enum<?>> String toDisplayName(@Nullable T enumValue) {
        if (enumValue == null) {
            return null;
        }

        var displayNameAnnotation = MoreBeans.findAnnotationNested(
                enumValue.getDeclaringClass(),
                enumValue.name(),
                DisplayName.class
        );

        return displayNameAnnotation.map(DisplayName::value)
                .orElseGet(() -> toDisplayName(enumValue.name()));
    }

    public static String toDisplayName(String name) {
        var noUnderscores = StringUtils.replace(name, "_", StringUtils.SPACE);

        return WordUtils.capitalizeFully(noUnderscores);
    }

}
