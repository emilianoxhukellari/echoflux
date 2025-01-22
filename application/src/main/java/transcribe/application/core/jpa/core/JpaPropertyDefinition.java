package transcribe.application.core.jpa.core;

import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import transcribe.core.core.bean.FieldProperty;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class JpaPropertyDefinition<DTO, T> {

    @Getter
    private final Set<JpaPropertyType> propertyTypes;
    private final PropertyDefinition<DTO, T> internalPropertyDefinition;
    private final FieldProperty fieldProperty;

    public JpaPropertyDefinition(PropertyDefinition<DTO, T> internalPropertyDefinition,
                                 FieldProperty fieldProperty,
                                 Set<JpaPropertyType> propertyTypes) {
        this.internalPropertyDefinition = Objects.requireNonNull(
                internalPropertyDefinition,
                "Internal property definition must not be null"
        );
        this.fieldProperty = Objects.requireNonNull(fieldProperty, "Field property must not be null");
        this.propertyTypes = Validate.notEmpty(propertyTypes, "Property types must not be empty");
    }

    public ValueProvider<DTO, T> getGetter() {
        return internalPropertyDefinition.getGetter();
    }

    public Optional<Setter<DTO, T>> getSetter() {
        return internalPropertyDefinition.getSetter();
    }

    public Class<T> getType() {
        return internalPropertyDefinition.getType();
    }

    public Class<?> getPropertyHolderType() {
        return internalPropertyDefinition.getPropertyHolderType();
    }

    public String getName() {
        return internalPropertyDefinition.getName();
    }

    public String getTopLevelName() {
        return internalPropertyDefinition.getTopLevelName();
    }

    public Field getField() {
        return fieldProperty.getField();
    }

    public Field getParentField() {
        return fieldProperty.getParentField();
    }

    public String getAttributeName() {
        return fieldProperty.getAttributeName();
    }

}
