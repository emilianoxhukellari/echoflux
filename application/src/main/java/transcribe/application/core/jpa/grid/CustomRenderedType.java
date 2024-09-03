package transcribe.application.core.jpa.grid;

import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.renderer.Renderer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@Getter
public enum CustomRenderedType {

    LOCAL_DATE_TIME(LocalDateTime.class, JpaGridRendererFactory::newLocalDateTimeRenderer),
    BOOLEAN(Boolean.class, JpaGridRendererFactory::newBooleanRenderer),
    COLLECTION(Collection.class, JpaGridRendererFactory::newCollectionRenderer);

    private final Class<?> type;
    private final Function<PropertyDefinition<?, ?>, Renderer<?>> rendererFactory;

    public static Optional<CustomRenderedType> ofAssignableType(Class<?> type) {
        return Arrays.stream(values())
                .filter(crt -> crt.getType().isAssignableFrom(type))
                .findFirst();
    }

    @SuppressWarnings("unchecked")
    public <T> Renderer<T> getRenderer(PropertyDefinition<T, ?> propertyDefinition) {
        return (Renderer<T>) rendererFactory.apply(propertyDefinition);
    }

}
