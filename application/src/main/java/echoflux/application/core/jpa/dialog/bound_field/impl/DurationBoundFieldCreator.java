package echoflux.application.core.jpa.dialog.bound_field.impl;

import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import echoflux.application.core.field.duration.DurationField;
import echoflux.application.core.jpa.core.JpaPropertyDefinition;
import echoflux.application.core.jpa.core.JpaPropertyDefinitionUtils;
import echoflux.application.core.jpa.core.JpaSupportedType;
import echoflux.application.core.jpa.dialog.bound_field.BoundFieldCreator;

import java.time.Duration;

@SpringComponent
public class DurationBoundFieldCreator implements BoundFieldCreator {

    @Override
    public <T, V> DurationField newBoundField(JpaPropertyDefinition<T, V> property,
                                                      Binder<T> binder,
                                                      boolean required) {
        var field = new DurationField();
        field.setLabel(JpaPropertyDefinitionUtils.toDisplayName(property));

        @SuppressWarnings("unchecked")
        var getter = (ValueProvider<T, Duration>) property.getGetter();
        @SuppressWarnings("unchecked")
        var setter = (Setter<T, Duration>) property.getSetter().orElse(null);

        var builder = binder.forField(field);
        if (required) {
            builder.asRequired();
        }
        builder.bind(getter, setter);

        return field;
    }

    @Override
    public boolean supportsType(JpaSupportedType type) {
        return JpaSupportedType.DURATION.equals(type);
    }

}
