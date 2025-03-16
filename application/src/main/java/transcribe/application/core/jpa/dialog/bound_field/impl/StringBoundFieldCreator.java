package transcribe.application.core.jpa.dialog.bound_field.impl;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import transcribe.application.core.jpa.core.JpaPropertyDefinition;
import transcribe.application.core.jpa.core.JpaSupportedType;
import transcribe.application.core.jpa.core.JpaPropertyDefinitionUtils;
import transcribe.application.core.jpa.dialog.bound_field.BoundFieldCreator;
import transcribe.application.core.annotation.BigText;
import transcribe.core.core.bean.MoreBeans;

@SpringComponent
public class StringBoundFieldCreator implements BoundFieldCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T, V> AbstractField<?, ?> newBoundField(JpaPropertyDefinition<T, V> property,
                                                    Binder<T> binder,
                                                    boolean required) {
        var field = MoreBeans.isAnnotationPresentNested(
                property.getPropertyHolderType(),
                property.getTopLevelName(),
                BigText.class
        )
                ? new TextArea(JpaPropertyDefinitionUtils.toDisplayName(property))
                : new TextField(JpaPropertyDefinitionUtils.toDisplayName(property));
        field.setMaxHeight("400px");

        var getter = (ValueProvider<T, String>) property.getGetter();
        var setter = (Setter<T, String>) property.getSetter().orElse(null);
        var builder = binder.forField(field);
        if (required) {
            builder.asRequired();
        }
        builder.bind(getter, setter);

        return field;
    }

    @Override
    public boolean supportsType(JpaSupportedType type) {
        return JpaSupportedType.STRING.equals(type);
    }

}
