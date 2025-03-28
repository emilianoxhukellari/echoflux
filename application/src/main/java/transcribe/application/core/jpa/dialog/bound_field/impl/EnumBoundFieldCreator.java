package transcribe.application.core.jpa.dialog.bound_field.impl;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import transcribe.application.core.jpa.core.JpaPropertyDefinition;
import transcribe.application.core.jpa.core.JpaSupportedType;
import transcribe.application.core.jpa.core.JpaPropertyDefinitionUtils;
import transcribe.application.core.jpa.dialog.bound_field.BoundFieldCreator;
import transcribe.core.core.utils.TsEnums;

@SpringComponent
public class EnumBoundFieldCreator implements BoundFieldCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T, V> AbstractField<ComboBox<Enum<?>>, Enum<?>> newBoundField(JpaPropertyDefinition<T, V> property,
                                                                          Binder<T> binder,
                                                                          boolean required) {
        var comboBox = new ComboBox<Enum<?>>(JpaPropertyDefinitionUtils.toDisplayName(property));
        comboBox.setItemLabelGenerator(TsEnums::toDisplayName);

        var enumType = (Class<Enum<?>>) property.getType();
        comboBox.setItems(enumType.getEnumConstants());

        var getter = (ValueProvider<T, Enum<?>>) property.getGetter();
        var setter = (Setter<T, Enum<?>>) property.getSetter().orElse(null);
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
