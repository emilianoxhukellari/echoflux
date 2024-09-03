package transcribe.application.core.jpa.dialog.bound_field.impl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import transcribe.application.core.jpa.core.JpaSupportedType;
import transcribe.application.core.jpa.dialog.bound_field.BoundFieldCreator;

@SpringComponent
public class EnumBoundFieldCreator implements BoundFieldCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T, V> Component newBoundField(PropertyDefinition<T, V> property, Binder<T> binder, boolean required) {
        var comboBox = new ComboBox<Enum<?>>(property.getCaption());
        var enumType = (Class<Enum<?>>) property.getType();
        comboBox.setItems(enumType.getEnumConstants());
        var getter = (ValueProvider<T, Enum<?>>) property.getGetter();
        var setter = (Setter<T, Enum<?>>) property.getSetter().orElseThrow();
        var builder = binder.forField(comboBox);
        if (required) {
            builder.asRequired();
        }
        builder.bind(getter, setter);

        return comboBox;
    }

    @Override
    public boolean supportsType(JpaSupportedType type) {
        return JpaSupportedType.ENUM.equals(type);
    }

}
