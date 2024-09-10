package transcribe.application.core.jpa.dialog.bound_field;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.PropertyDefinition;
import lombok.extern.slf4j.Slf4j;
import transcribe.application.core.jpa.core.JpaSupportedType;
import transcribe.application.core.spring.SpringContext;
import transcribe.domain.bean.BeanUtils;

import java.util.Collection;

@Slf4j
public final class JpaSaveDialogFieldFactory {

    private static final Collection<BoundFieldCreator> fieldCreators = SpringContext.get().getBeansOfType(BoundFieldCreator.class).values();

    public static <T> Component newBoundField(PropertyDefinition<T, ?> property, Binder<T> binder) {
        var required = BeanUtils.isFieldRequired(property.getPropertyHolderType(), property.getName());
        var type = JpaSupportedType.ofBeanType(property.getType());

        var fieldCreator = fieldCreators.stream()
                .filter(c -> c.supportsType(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported field type: " + property.getType()));

        return fieldCreator.newBoundField(property, binder, required);
    }

}
