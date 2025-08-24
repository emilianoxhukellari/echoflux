package echoflux.application.core.jooq.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.data.renderer.Renderer;
import echoflux.application.core.jooq.grid.JooqGridRendererFactory;
import echoflux.core.core.validate.guard.Guard;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jooq.Field;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum JooqPropertyType {

    STRING(String.class, false, ColumnTextAlign.START, JooqGridRendererFactory::newGenericRenderer),
    ZONE_ID(ZoneId.class, false, ColumnTextAlign.START, JooqGridRendererFactory::newGenericRenderer),
    LONG(Long.class, false, ColumnTextAlign.END, JooqGridRendererFactory::newGenericRenderer),
    INTEGER(Integer.class, false, ColumnTextAlign.END, JooqGridRendererFactory::newGenericRenderer),
    DOUBLE(Double.class, false, ColumnTextAlign.END,  JooqGridRendererFactory::newGenericRenderer),
    FLOAT(Float.class, false, ColumnTextAlign.END,  JooqGridRendererFactory::newGenericRenderer),
    BOOLEAN(Boolean.class, false, ColumnTextAlign.CENTER, JooqGridRendererFactory::newBooleanRenderer),
    OFFSET_DATE_TIME(OffsetDateTime.class, false, ColumnTextAlign.END, JooqGridRendererFactory::newOffsetDateTimeRenderer),
    URI(java.net.URI.class, false, ColumnTextAlign.START, JooqGridRendererFactory::newGenericRenderer),
    JSON(JsonNode.class, false, ColumnTextAlign.START,  JooqGridRendererFactory::newGenericRenderer),
    DURATION(Duration.class, false, ColumnTextAlign.END, JooqGridRendererFactory::newDurationRenderer),
    ENUM(Enum.class, false, ColumnTextAlign.START, JooqGridRendererFactory::newEnumRenderer),
    ENUM_ARRAY(Enum.class, true, ColumnTextAlign.START, JooqGridRendererFactory::newEnumArrayRenderer),
    STRING_ARRAY(String.class, true, ColumnTextAlign.START, JooqGridRendererFactory::newGenericArrayRenderer);

    /**
     * This is the component type for arrays and the bean type for non-array fields.
     * */
    private final Class<?> type;
    private final boolean isArray;
    private final ColumnTextAlign columnTextAlign;
    private final Function<Field<?>, Renderer<?>> defaultRendererFactory;

    public static <T> JooqPropertyType fromField(Field<T> field) {
        Guard.notNull(field, "field cannot be null to resolve JooqPropertyType");

        if (field.getDataType().isArray()) {
            return Arrays.stream(values())
                    .filter(JooqPropertyType::isArray)
                    .filter(t -> t.getType().isAssignableFrom(field.getType().getComponentType()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Unsupported array type [%s] for field [%s]".formatted(field.getType(), field.getName())));
        }

        return Arrays.stream(values())
                .filter(Predicate.not(JooqPropertyType::isArray))
                .filter(t -> t.getType().isAssignableFrom(field.getType()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Unsupported bean type [%s] for field [%s]".formatted(field.getType(), field.getName())));
    }

}
