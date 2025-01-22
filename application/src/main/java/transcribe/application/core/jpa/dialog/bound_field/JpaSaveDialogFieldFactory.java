package transcribe.application.core.jpa.dialog.bound_field;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.Binder;
import transcribe.application.core.jpa.core.JpaPropertyDefinition;
import transcribe.application.core.jpa.core.JpaSupportedType;
import transcribe.application.core.spring.SpringContext;
import transcribe.core.core.bean.utils.MoreBeans;

import java.util.Collection;

public final class JpaSaveDialogFieldFactory {

    private static final Collection<BoundFieldCreator> fieldCreators = SpringContext.get()
            .getBeansOfType(BoundFieldCreator.class)
            .values();

    public static <T> Component newBoundField(JpaPropertyDefinition<T, ?> property, Binder<T> binder, Class<T> beanType) {
        var required = MoreBeans.isFieldRequiredNested(beanType, property.getName());
        var type = JpaSupportedType.ofBeanType(property.getType());

        var fieldCreator = fieldCreators.stream()
                .filter(c -> c.supportsType(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported field type: " + property.getType()));

        var field = fieldCreator.newBoundField(property, binder, required);
        field.setReadOnly(property.getSetter().isEmpty());

        return field;
    }

}
