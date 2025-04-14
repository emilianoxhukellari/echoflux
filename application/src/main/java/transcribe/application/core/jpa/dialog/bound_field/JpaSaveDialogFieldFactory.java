package transcribe.application.core.jpa.dialog.bound_field;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.Binder;
import transcribe.application.core.jpa.core.JpaPropertyDefinition;
import transcribe.application.core.jpa.core.JpaSupportedType;
import transcribe.core.core.bean.MoreBeans;
import transcribe.core.core.bean.loader.BeanLoader;

public final class JpaSaveDialogFieldFactory {

    public static <T> Component newBoundField(JpaPropertyDefinition<T, ?> property,
                                              Binder<T> binder,
                                              Class<T> beanType,
                                              BeanLoader beanLoader) {
        var required = MoreBeans.isFieldRequiredNested(beanType, property.getName());
        var type = JpaSupportedType.ofBeanType(property.getType());

        var fieldCreator = beanLoader.findWhen(BoundFieldCreator.class, c -> c.supportsType(type))
                .orElseThrow(() -> new IllegalStateException("No field creator found for type " + type));

        var field = fieldCreator.newBoundField(property, binder, required);
        field.setReadOnly(property.getSetter().isEmpty());

        return field;
    }

}
