package transcribe.application.core.jpa.dialog.bound_field.impl;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import transcribe.application.core.jpa.core.JpaSupportedType;
import transcribe.application.core.jpa.dialog.bound_field.BoundFieldCreator;

import java.time.LocalDate;

@SpringComponent
public class LocalDateBoundFieldCreator implements BoundFieldCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T, V> AbstractField<DatePicker, LocalDate> newBoundField(PropertyDefinition<T, V> property,
                                                                     Binder<T> binder,
                                                                     boolean required) {
        var field = new DatePicker(property.getCaption());
        var getter = (ValueProvider<T, LocalDate>) property.getGetter();
        var setter = (Setter<T, LocalDate>) property.getSetter().orElse(null);
        var builder = binder.forField(field);
        if (required) {
            builder.asRequired();
        }
        builder.bind(getter, setter);

        return field;
    }

    @Override
    public boolean supportsType(JpaSupportedType type) {
        return JpaSupportedType.LOCAL_DATE.equals(type);
    }

}
