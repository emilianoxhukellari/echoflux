package transcribe.application.core.jpa.dialog.bound_field;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.PropertyDefinition;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.application.core.jpa.core.JpaSupportedType;

@Validated
public interface BoundFieldCreator {

    <T, V> Component newBoundField(@NotNull PropertyDefinition<T, V> property, @NotNull Binder<T> binder, boolean required);

    boolean supportsType(JpaSupportedType type);

}
