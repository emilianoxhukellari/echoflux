package transcribe.application.core.jpa.dialog.bound_field.impl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import transcribe.application.core.jpa.core.JpaSupportedType;
import transcribe.application.core.jpa.dialog.bound_field.BoundFieldCreator;

@SpringComponent
public class FloatBoundFieldCreator implements BoundFieldCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T, V> Component newBoundField(PropertyDefinition<T, V> property, Binder<T> binder, boolean required) {
        var field = new NumberField(property.getCaption());
        var getter = (ValueProvider<T, Float>) property.getGetter();
        var setter = (Setter<T, Float>) property.getSetter().orElseThrow();
        var builder = binder.forField(field).withConverter(
                Converter.from(
                        v -> Result.ok(v == null ? null : v.floatValue()),
                        v -> v == null ? null : v.doubleValue()
                )
        );
        if (required) {
            builder.asRequired();
        }
        builder.bind(getter, setter);

        return field;
    }

    @Override
    public boolean supportsType(JpaSupportedType type) {
        return JpaSupportedType.FLOAT.equals(type);
    }

}
