package echoflux.application.core.jpa.grid;

import com.vaadin.flow.component.icon.AbstractIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.function.ValueProvider;
import echoflux.application.security.AuthenticatedUser;
import echoflux.core.core.validate.guard.Guard;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import echoflux.application.core.icon.IconFactory;
import echoflux.core.core.utils.MoreDurations;
import echoflux.core.core.utils.MoreEnums;
import org.springframework.data.util.TypeInformation;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.stream.Collectors;

public class JpaGridRendererFactory {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

    public static <T> LocalDateTimeRenderer<T> newLocalDateTimeRenderer(PropertyDefinition<T, ?> propertyDefinition) {
        Guard.notNull(propertyDefinition);
        Guard.assignableFrom(LocalDateTime.class, propertyDefinition.getType());

        return new LocalDateTimeRenderer<>(
                (ValueProvider<T, LocalDateTime>) item -> (LocalDateTime) propertyDefinition.getGetter().apply(item),
                () -> DATE_TIME_FORMATTER
        );
    }

    public static <T> LocalDateRenderer<T> newLocalDateRenderer(PropertyDefinition<T, ?> propertyDefinition) {
        Guard.notNull(propertyDefinition);
        Guard.assignableFrom(LocalDate.class, propertyDefinition.getType());

        return new LocalDateRenderer<>(
                (ValueProvider<T, LocalDate>) item -> (LocalDate) propertyDefinition.getGetter().apply(item),
                () -> DATE_FORMATTER
        );
    }

    public static <T> TextRenderer<T> newGenericRenderer(PropertyDefinition<T, ?> propertyDefinition) {
        Guard.notNull(propertyDefinition);

        return new TextRenderer<>(item -> {
            var value = propertyDefinition.getGetter().apply(item);
            if (value == null) {
                return StringUtils.EMPTY;
            }

            return value.toString();
        });
    }

    public static <T> TextRenderer<T> newInstantRenderer(PropertyDefinition<T, ?> propertyDefinition) {
        Guard.notNull(propertyDefinition);
        Guard.assignableFrom(Instant.class, propertyDefinition.getType());

        return new TextRenderer<>(item -> {
            var value = (Instant) propertyDefinition.getGetter().apply(item);
            if (value == null) {
                return StringUtils.EMPTY;
            }

            var zonedDateTime = value.atZone(AuthenticatedUser.getZoneId());

            return zonedDateTime.format(DATE_TIME_FORMATTER);
        });
    }

    public static <T> ComponentRenderer<AbstractIcon<?>, T> newBooleanRenderer(PropertyDefinition<T, ?> propertyDefinition) {
        Guard.notNull(propertyDefinition);
        Guard.assignableFrom(Boolean.class, propertyDefinition.getType());

        return new ComponentRenderer<>(
                item -> {
                    var value = (Boolean) propertyDefinition.getGetter().apply(item);
                    if (value == null) {
                        return IconFactory.newIcon(VaadinIcon.MINUS::create, "gray", "1.5rem", "Unknown");
                    } else if (value) {
                        return IconFactory.newIcon(VaadinIcon.CHECK::create, "green", "1.5rem", "Yes");
                    } else {
                        return IconFactory.newIcon(VaadinIcon.CLOSE::create, "red", "1.5rem", "No");
                    }
                }
        );
    }

    public static <T> TextRenderer<T> newCollectionRenderer(PropertyDefinition<T, ?> propertyDefinition, TypeInformation<?> typeInformation) {
        Guard.notNull(typeInformation);
        Guard.assignableFrom(Collection.class, propertyDefinition.getType());

        return new TextRenderer<>(item -> {
            var value = (Collection<?>) propertyDefinition.getGetter().apply(item);
            if (CollectionUtils.isEmpty(value)) {
                return StringUtils.EMPTY;
            } else {
                var genericType = typeInformation.getRequiredComponentType().getType();

                if (genericType.isEnum()) {
                    return value.stream()
                            .map(v -> MoreEnums.toDisplayName((Enum<?>) v))
                            .collect(Collectors.joining(", "));
                }

                return StringUtils.join(value, ", ");
            }
        });
    }

    public static <T> TextRenderer<T> newEnumRenderer(PropertyDefinition<T, ?> propertyDefinition) {
        Guard.notNull(propertyDefinition);
        Guard.assignableFrom(Enum.class, propertyDefinition.getType());

        return new TextRenderer<>(item -> {
            var value = (Enum<?>) propertyDefinition.getGetter().apply(item);

            return MoreEnums.toDisplayName(value);
        });
    }

    public static <T> TextRenderer<T> newDurationRenderer(PropertyDefinition<T, ?> propertyDefinition) {
        Guard.notNull(propertyDefinition);
        Guard.assignableFrom(Duration.class, propertyDefinition.getType());

        return new TextRenderer<>(item -> {
            var duration = (Duration) propertyDefinition.getGetter().apply(item);

            if (duration == null) {
                return StringUtils.EMPTY;
            }

            return MoreDurations.format(duration);
        });
    }

}
