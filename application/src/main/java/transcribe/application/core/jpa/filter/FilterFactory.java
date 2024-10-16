package transcribe.application.core.jpa.filter;

import com.vaadin.flow.data.binder.PropertyDefinition;
import org.apache.commons.lang3.Validate;
import transcribe.application.core.jpa.core.JpaSupportedType;
import transcribe.application.core.jpa.filter.impl.BooleanJpaFilter;
import transcribe.application.core.jpa.filter.impl.NumberJpaFilter;
import transcribe.application.core.jpa.filter.impl.EnumJpaFilter;
import transcribe.application.core.jpa.filter.impl.LocalDateJpaFilter;
import transcribe.application.core.jpa.filter.impl.TextJpaFilter;
import transcribe.domain.core.bean.BeanUtils;

import java.util.Objects;

public final class FilterFactory {

    public static <T> JpaFilter<T> newFilter(PropertyDefinition<T, ?> propertyDefinition) {
        Objects.requireNonNull(propertyDefinition, "Property definition is required");

        return newFilter(
                propertyDefinition.getName(),
                propertyDefinition.getType(),
                propertyDefinition.getPropertyHolderType(),
                false
        );
    }

    public static <T> JpaFilter<T> newFilter(String propertyName,
                                             Class<?> propertyType,
                                             Class<?> propertyHolderType,
                                             boolean asCollection) {
        Validate.notBlank(propertyName, "Property is required");
        Objects.requireNonNull(propertyHolderType, "Property holder type is required");
        var supportedType = JpaSupportedType.ofBeanType(propertyType);

        return switch (supportedType) {
            case STRING, URI, JSON -> new TextJpaFilter<>(propertyName, asCollection);
            case BOOLEAN -> new BooleanJpaFilter<>(propertyName, asCollection);
            case ENUM -> new EnumJpaFilter<>(propertyName, propertyType, asCollection);
            case LOCAL_DATE, LOCAL_DATE_TIME -> new LocalDateJpaFilter<>(propertyName, asCollection);
            case DOUBLE, FLOAT, LONG, INTEGER -> new NumberJpaFilter<>(propertyName, asCollection);
            case COLLECTION -> {
                if (asCollection) {
                    throw new UnsupportedOperationException("Collection filter does not support nested collections");
                }
                var genericType = BeanUtils.getGenericType(propertyHolderType, propertyName);

                yield newFilter(propertyName, genericType, propertyHolderType, true);
            }
        };
    }

}
