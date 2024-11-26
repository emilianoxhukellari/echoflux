package transcribe.application.core.jpa.dialog.bound_field.impl;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import transcribe.application.core.jpa.core.JpaSupportedType;
import transcribe.application.core.jpa.dialog.bound_field.BoundFieldCreator;

import java.time.LocalDateTime;

@SpringComponent
public class LocalDateTimeBoundFieldCreator implements BoundFieldCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T, V> AbstractField<DateTimePicker, LocalDateTime> newBoundField(PropertyDefinition<T, V> property,
                                                                             Binder<T> binder,
                                                                             boolean required) {
        var field = new DateTimePicker(property.getCaption());
        var getter = (ValueProvider<T, LocalDateTime>) property.getGetter();
        var setter = (Setter<T, LocalDateTime>) property.getSetter().orElse(null);
        var builder = binder.forField(field);
        if (required) {
            builder.asRequired();
        }
        builder.bind(getter, setter);

        return field;
    }

    @Override
    public boolean supportsType(JpaSupportedType type) {
        return JpaSupportedType.LOCAL_DATE_TIME.equals(type);
    }

}
