package transcribe.application.core.jpa.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.vaadin.flow.data.renderer.Renderer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import transcribe.application.core.jpa.grid.JpaGridRendererFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public enum JpaSupportedType {

    STRING(String.class, null),
    BOOLEAN(Boolean.class, JpaGridRendererFactory::newBooleanRenderer),
    ENUM(Enum.class, JpaGridRendererFactory::newEnumRenderer),
    LOCAL_DATE_TIME(LocalDateTime.class, JpaGridRendererFactory::newLocalDateTimeRenderer),
    LOCAL_DATE(LocalDateTime.class, null),
    DOUBLE(Double.class, null),
    LONG(Long.class, null),
    FLOAT(Float.class, null),
    INTEGER(Integer.class, null),
    URI(java.net.URI.class, null),
    JSON(JsonNode.class, null),
    COLLECTION(Collection.class, JpaGridRendererFactory::newCollectionRenderer);

    @Getter
    private final Class<?> type;
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
