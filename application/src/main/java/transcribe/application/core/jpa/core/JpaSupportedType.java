package transcribe.application.core.jpa.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.data.renderer.Renderer;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import transcribe.application.core.jpa.grid.JpaGridRendererFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public enum JpaSupportedType {

    STRING(String.class, ColumnTextAlign.START, null),
    BOOLEAN(Boolean.class, ColumnTextAlign.START, JpaGridRendererFactory::newBooleanRenderer),
    ENUM(Enum.class, ColumnTextAlign.START, JpaGridRendererFactory::newEnumRenderer),
    LOCAL_DATE_TIME(LocalDateTime.class, ColumnTextAlign.START, JpaGridRendererFactory::newLocalDateTimeRenderer),
    LOCAL_DATE(LocalDateTime.class, ColumnTextAlign.START, null),
    DOUBLE(Double.class, ColumnTextAlign.END, null),
    LONG(Long.class, ColumnTextAlign.END, null),
    FLOAT(Float.class, ColumnTextAlign.END, null),
    INTEGER(Integer.class, ColumnTextAlign.END, null),
    URI(java.net.URI.class, ColumnTextAlign.START, null),
    JSON(JsonNode.class, ColumnTextAlign.START, null),
    DURATION(Duration.class, ColumnTextAlign.END, JpaGridRendererFactory::newDurationRenderer),
    COLLECTION(Collection.class, ColumnTextAlign.START, JpaGridRendererFactory::newCollectionRenderer);

    @Getter
    private final Class<?> type;

    @Getter
    private final ColumnTextAlign columnTextAlign;

    @Nullable
    private final Function<JpaPropertyDefinition<?, ?>, Renderer<?>> customRendererFactory;

    /**
     * @throws NoSuchElementException if the given type is not supported
     */
    public static JpaSupportedType ofBeanType(Class<?> beanType) {
        return Arrays.stream(values())
                .filter(t -> t.getType().isAssignableFrom(beanType))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("""
                                Unsupported jpa type [%s]. If you intend to read the
                                internal fields of this type, use @ParentProperty.
                                """.formatted(beanType)
                        )
                );

    }

    public Optional<Function<JpaPropertyDefinition<?, ?>, Renderer<?>>> findCustomRendererFactory() {
        return Optional.ofNullable(customRendererFactory);
    }

}
