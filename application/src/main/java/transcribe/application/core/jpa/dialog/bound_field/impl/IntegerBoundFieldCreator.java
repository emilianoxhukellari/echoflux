package transcribe.application.core.jpa.dialog.bound_field.impl;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import transcribe.application.core.jpa.core.JpaSupportedType;
import transcribe.application.core.jpa.dialog.bound_field.BoundFieldCreator;

@SpringComponent
public class IntegerBoundFieldCreator implements BoundFieldCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T, V> AbstractField<IntegerField, Integer> newBoundField(PropertyDefinition<T, V> property,
                                                                     Binder<T> binder,
                                                                     boolean required) {
        var field = new IntegerField(property.getCaption());
        var getter = (ValueProvider<T, Integer>) property.getGetter();
        var setter = (Setter<T, Integer>) property.getSetter().orElse(null);
        var builder = binder.forField(field);
        if (required) {
            builder.asRequired();
        }
        builder.bind(getter, setter);

        return field;
    }

    @Override
    public boolean supportsType(JpaSupportedType type) {
        return JpaSupportedType.INTEGER.equals(type);
    }
}
