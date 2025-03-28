package transcribe.application.core.jpa.grid;

import com.vaadin.flow.component.icon.AbstractIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.function.ValueProvider;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import transcribe.application.core.icon.IconFactory;
import transcribe.application.core.jpa.core.JpaPropertyDefinition;
import transcribe.core.core.utils.TsEnums;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.Objects;

public class JpaGridRendererFactory {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM);

    public static <T> LocalDateTimeRenderer<T> newLocalDateTimeRenderer(JpaPropertyDefinition<T, ?> propertyDefinition) {
        Objects.requireNonNull(propertyDefinition, "Property definition is required");
        Validate.isTrue(LocalDateTime.class.isAssignableFrom(propertyDefinition.getType()),
                "Property definition must be of assignable to LocalDateTime");

        return new LocalDateTimeRenderer<>(
                (ValueProvider<T, LocalDateTime>) item -> (LocalDateTime) propertyDefinition.getGetter().apply(item),
                () -> DATE_TIME_FORMATTER
        );
    }

    public static <T> ComponentRenderer<AbstractIcon<?>, T> newBooleanRenderer(JpaPropertyDefinition<T, ?> propertyDefinition) {
        Objects.requireNonNull(propertyDefinition, "Property definition is required");
        Validate.isTrue(Boolean.class.isAssignableFrom(propertyDefinition.getType()),
                "Property definition must be of assignable to Boolean");

        return new ComponentRenderer<>(
                item -> {
                    var value = (Boolean) propertyDefinition.getGetter().apply(item);
                    if (value == null) {
                        return IconFactory.newIcon(VaadinIcon.MINUS::create, "gray", "1.5rem", "Unknown");
                    } else if (value) {
                        return IconFactory.newIcon(VaadinIcon.CHECK::create, "green", "1.5rem",  "Yes");
                    } else {
                        return IconFactory.newIcon(VaadinIcon.CLOSE::create, "red", "1.5rem",  "No");
                    }
                }
        );
    }

    public static <T> TextRenderer<T> newCollectionRenderer(JpaPropertyDefinition<T, ?> propertyDefinition) {
        Objects.requireNonNull(propertyDefinition, "Property definition is required");
        Validate.isTrue(Collection.class.isAssignableFrom(propertyDefinition.getType()),
                "Property definition must be of assignable to Collection");

        return new TextRenderer<>(item -> {
            var value = (Collection<?>) propertyDefinition.getGetter().apply(item);
            if (CollectionUtils.isEmpty(value)) {
                return StringUtils.EMPTY;
            } else {
                return StringUtils.join(value, ", ");
            }
        });
    }

    public static <T> TextRenderer<T> newEnumRenderer(JpaPropertyDefinition<T, ?> propertyDefinition) {
        Objects.requireNonNull(propertyDefinition, "Property definition is required");
        Validate.isTrue(Enum.class.isAssignableFrom(propertyDefinition.getType()),
                "Property definition must be of assignable to Enum");

        return new TextRenderer<>(item -> {
            var value = (Enum<?>) propertyDefinition.getGetter().apply(item);

            return TsEnums.toDisplayName(value);
        });
    }

}
