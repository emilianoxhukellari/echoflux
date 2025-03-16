package transcribe.application.core.jpa.dialog.bound_field.impl;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import transcribe.application.core.jpa.core.JpaPropertyDefinition;
import transcribe.application.core.jpa.core.JpaPropertyDefinitionUtils;
import transcribe.application.core.jpa.core.JpaSupportedType;
import transcribe.application.core.jpa.dialog.bound_field.BoundFieldCreator;
import transcribe.core.core.bean.MoreBeans;
import transcribe.core.core.utils.MoreEnums;
import transcribe.core.core.validate.guard.Guard;

import java.util.Set;

@SpringComponent
public class MultiSelectEnumBoundFieldCreator implements BoundFieldCreator {

    public <T, V> AbstractField<?, Set<Enum<?>>> newBoundField(JpaPropertyDefinition<T, V> property,
                                                               Binder<T> binder,
                                                               boolean required) {
        var genericType = MoreBeans.getGenericTypeNested(property.getPropertyHolderType(), property.getTopLevelName());
        var enumType = Guard.enumType(genericType);

        var multiSelectComboBox = new MultiSelectComboBox<Enum<?>>(JpaPropertyDefinitionUtils.toDisplayName(property));
        multiSelectComboBox.setItemLabelGenerator(MoreEnums::toDisplayName);
        multiSelectComboBox.setItems(enumType.getEnumConstants());

        @SuppressWarnings("unchecked")
        var getter = (ValueProvider<T, Set<Enum<?>>>) property.getGetter();
        @SuppressWarnings("unchecked")
        var setter = (Setter<T, Set<Enum<?>>>) property.getSetter().orElse(null);

        var builder = binder.forField(multiSelectComboBox);
        if (required) {
            builder.asRequired();
        }
        builder.bind(getter, setter);

        return multiSelectComboBox;
    }

    @Override
    public boolean supportsType(JpaSupportedType type) {
        return JpaSupportedType.COLLECTION.equals(type);
    }

}
