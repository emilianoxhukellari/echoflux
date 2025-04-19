package echoflux.application.core.jpa.dialog.bound_field.impl;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import echoflux.application.core.jpa.core.JpaPropertyDefinition;
import echoflux.application.core.jpa.core.JpaSupportedType;
import echoflux.application.core.jpa.core.JpaPropertyDefinitionUtils;
import echoflux.application.core.jpa.dialog.bound_field.BoundFieldCreator;

@SpringComponent
public class LongBoundFieldCreator implements BoundFieldCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T, V> AbstractField<NumberField, Double> newBoundField(JpaPropertyDefinition<T, V> property,
                                                                   Binder<T> binder,
                                                                   boolean required) {
        var field = new NumberField(JpaPropertyDefinitionUtils.toDisplayName(property));
        var getter = (ValueProvider<T, Long>) property.getGetter();
        var setter = (Setter<T, Long>) property.getSetter().orElse(null);
        var builder = binder.forField(field).withConverter(
                Converter.from(
                        v -> Result.ok(v == null ? null : v.longValue()),
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
        return JpaSupportedType.LONG.equals(type);
    }

}
