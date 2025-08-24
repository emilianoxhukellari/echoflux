package echoflux.application.core.jooq.grid;

import com.vaadin.flow.component.icon.AbstractIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import echoflux.application.core.icon.IconFactory;
import echoflux.application.core.security.AuthenticatedUser;
import echoflux.core.core.utils.MoreDurations;
import echoflux.core.core.utils.MoreEnums;
import echoflux.core.core.utils.MoreStrings;
import echoflux.core.core.validate.guard.Guard;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Field;
import org.jooq.Record;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class JooqGridRendererFactory {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM);

    public static <R extends Record> TextRenderer<R> newGenericRenderer(Field<?> field) {
        Guard.notNull(field);

        return new TextRenderer<>(r -> {
            var value = r.get(field);

            return Objects.toString(value, MoreStrings.EMPTY);
        });
    }

    public static <R extends Record> TextRenderer<R> newGenericArrayRenderer(Field<?> field) {
        Guard.notNull(field);
        Guard.isTrue(field.getType().isArray());

        return new TextRenderer<>(r -> {
            var value = (Object[]) r.get(field);

            if (ArrayUtils.isEmpty(value)) {
                return MoreStrings.EMPTY;
            } else {
                return StringUtils.join(value, ", ");
            }
        });
    }

    public static <R extends Record> TextRenderer<R> newOffsetDateTimeRenderer(Field<?> field) {
        Guard.notNull(field);
        Guard.assignableFrom(OffsetDateTime.class, field.getType());

        return new TextRenderer<>(r -> {
            var value = (OffsetDateTime) r.get(field);
            if (value == null) {
                return MoreStrings.EMPTY;
            }

            var zonedDateTime = value.atZoneSameInstant(AuthenticatedUser.getZoneId());

            return zonedDateTime.format(DATE_TIME_FORMATTER);
        });
    }

    public static <R extends Record> ComponentRenderer<AbstractIcon<?>, R> newBooleanRenderer(Field<?> field) {
        Guard.notNull(field);
        Guard.assignableFrom(Boolean.class, field.getType());

        return new ComponentRenderer<>(
                r -> {
                    var value = (Boolean) r.get(field);
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

    public static <R extends Record> TextRenderer<R> newEnumArrayRenderer(Field<?> field) {
        Guard.notNull(field);
        Guard.isTrue(field.getType().isArray());
        Guard.assignableFrom(Enum.class, field.getDataType().getArrayComponentType());

        return new TextRenderer<>(r -> {
            var value = (Object[]) r.get(field);

            if (ArrayUtils.isEmpty(value)) {
                return MoreStrings.EMPTY;
            } else {
                return Arrays.stream(value)
                        .map(v -> MoreEnums.toDisplayName((Enum<?>) v))
                        .collect(Collectors.joining(", "));
            }
        });
    }

    public static <R extends Record> TextRenderer<R> newEnumRenderer(Field<?> field) {
        Guard.notNull(field);
        Guard.assignableFrom(Enum.class, field.getType());

        return new TextRenderer<>(r -> {
            var value = (Enum<?>) r.get(field);

            return MoreEnums.toDisplayName(value);
        });
    }

    public static <R extends Record> TextRenderer<R> newDurationRenderer(Field<?> field) {
        Guard.notNull(field);
        Guard.assignableFrom(Duration.class, field.getType());

        return new TextRenderer<>(r -> {
            var duration = (Duration) r.get(field);

            if (duration == null) {
                return MoreStrings.EMPTY;
            }

            return MoreDurations.format(duration);
        });
    }

}
