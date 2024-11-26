package transcribe.application.core.jpa.dialog.bound_field.impl;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import transcribe.application.core.jpa.core.JpaSupportedType;
import transcribe.application.core.jpa.dialog.bound_field.BoundFieldCreator;

@SpringComponent
public class BooleanBoundFieldCreator implements BoundFieldCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T, V> AbstractField<Checkbox, Boolean> newBoundField(PropertyDefinition<T, V> property,
                                                                 Binder<T> binder,
                                                                 boolean required) {
        var field = new Checkbox(property.getCaption());
        var getter = (ValueProvider<T, Boolean>) property.getGetter();
        var setter = (Setter<T, Boolean>) property.getSetter().orElse(null);
        binder.forField(field)
                .bind(getter, setter);

        return field;
    }

    @Override
    public boolean supportsType(JpaSupportedType type) {
        return JpaSupportedType.BOOLEAN.equals(type);
    }

}
