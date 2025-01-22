package transcribe.application.core.jpa.dialog.bound_field;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.data.binder.Binder;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.application.core.jpa.core.JpaPropertyDefinition;
import transcribe.application.core.jpa.core.JpaSupportedType;

@Validated
public interface BoundFieldCreator {

    <T, V> AbstractField<?, ?> newBoundField(@NotNull JpaPropertyDefinition<T, V> property,
                                             @NotNull Binder<T> binder,
                                             boolean required);

    boolean supportsType(JpaSupportedType type);

}
