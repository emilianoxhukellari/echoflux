package echoflux.application.core.jpa.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.renderer.Renderer;
import echoflux.application.core.jpa.grid.JpaGridRendererFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.TypeInformation;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

@Getter
@RequiredArgsConstructor
public enum JpaPropertyType {

    STRING(String.class, ColumnTextAlign.START, (p, _) -> JpaGridRendererFactory.newGenericRenderer(p)),
    ZONE_ID(ZoneId.class, ColumnTextAlign.START, (p, _) -> JpaGridRendererFactory.newGenericRenderer(p)),
    LONG(Long.class, ColumnTextAlign.END, (p, _) -> JpaGridRendererFactory.newGenericRenderer(p)),
    INTEGER(Integer.class, ColumnTextAlign.END, (p, _) -> JpaGridRendererFactory.newGenericRenderer(p)),
    DOUBLE(Double.class, ColumnTextAlign.END, (p, _) -> JpaGridRendererFactory.newGenericRenderer(p)),
    FLOAT(Float.class, ColumnTextAlign.END, (p, _) -> JpaGridRendererFactory.newGenericRenderer(p)),
    BOOLEAN(Boolean.class, ColumnTextAlign.CENTER, (p, _) -> JpaGridRendererFactory.newBooleanRenderer(p)),
    LOCAL_DATE_TIME(LocalDateTime.class, ColumnTextAlign.END, (p, _) -> JpaGridRendererFactory.newLocalDateTimeRenderer(p)),
    LOCAL_DATE(LocalDate.class, ColumnTextAlign.END, (p, _) -> JpaGridRendererFactory.newLocalDateRenderer(p)),
    INSTANT(Instant.class, ColumnTextAlign.END, (p, _) -> JpaGridRendererFactory.newInstantRenderer(p)),
    URI(java.net.URI.class, ColumnTextAlign.START, (p, _) -> JpaGridRendererFactory.newGenericRenderer(p)),
    JSON(JsonNode.class, ColumnTextAlign.START, (p, _) -> JpaGridRendererFactory.newGenericRenderer(p)),
    DURATION(Duration.class, ColumnTextAlign.END, (p, _) -> JpaGridRendererFactory.newDurationRenderer(p)),
    ENUM(Enum.class, ColumnTextAlign.START, (p, _) -> JpaGridRendererFactory.newEnumRenderer(p)),
    COLLECTION(Collection.class, ColumnTextAlign.START, JpaGridRendererFactory::newCollectionRenderer);

    private final Class<?> beanType;
    private final ColumnTextAlign columnTextAlign;
    private final BiFunction<PropertyDefinition<?, ?>, TypeInformation<?>, Renderer<?>> defaultRendererFactory;

    public static JpaPropertyType ofBeanType(Class<?> beanType) {
        return Arrays.stream(values())
                .filter(t -> t.getBeanType().isAssignableFrom(beanType))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Unsupported jpa property type [%s]".formatted(beanType)));
    }

}
