package transcribe.application.core.jpa.filter;

import org.apache.commons.lang3.Validate;
import transcribe.application.core.jpa.core.JpaPropertyDefinition;
import transcribe.application.core.jpa.core.JpaSupportedType;
import transcribe.application.core.jpa.filter.impl.BooleanJpaFilter;
import transcribe.application.core.jpa.filter.impl.NumberJpaFilter;
import transcribe.application.core.jpa.filter.impl.EnumJpaFilter;
import transcribe.application.core.jpa.filter.impl.LocalDateJpaFilter;
import transcribe.application.core.jpa.filter.impl.TextJpaFilter;
import transcribe.core.core.bean.MoreBeans;

import java.util.Objects;

public final class FilterFactory {

    public static <DTO, ENTITY> JpaFilter<ENTITY> newFilter(JpaPropertyDefinition<DTO, ?> propertyDefinition) {
        Objects.requireNonNull(propertyDefinition, "Property definition is required");

        return newFilter(
                propertyDefinition.getAttributeName(),
                propertyDefinition.getName(),
                propertyDefinition.getTopLevelName(),
                propertyDefinition.getType(),
                propertyDefinition.getPropertyHolderType(),
                false
        );
    }

    public static <ENTITY> JpaFilter<ENTITY> newFilter(String attributeName,
                                                       String propertyName,
                                                       String propertyTopLevelName,
                                                       Class<?> propertyType,
                                                       Class<?> propertyHolderType,
                                                       boolean asCollection) {
        Validate.notBlank(attributeName, "Attribute name is required");
        Objects.requireNonNull(propertyHolderType, "Property holder type is required");
        var supportedType = JpaSupportedType.ofBeanType(propertyType);

        return switch (supportedType) {
            case STRING, URI, JSON -> new TextJpaFilter<>(attributeName, propertyName, asCollection);
            case BOOLEAN -> new BooleanJpaFilter<>(attributeName, propertyName, asCollection);
            case ENUM -> new EnumJpaFilter<>(attributeName, propertyName, propertyType, asCollection);
            case LOCAL_DATE, LOCAL_DATE_TIME -> new LocalDateJpaFilter<>(attributeName, propertyName, asCollection);
            case DOUBLE, FLOAT, LONG, INTEGER -> new NumberJpaFilter<>(attributeName, propertyName, asCollection);
            case COLLECTION -> {
                if (asCollection) {
                    throw new UnsupportedOperationException("Collection filter does not support nested collections");
                }
                var genericType = MoreBeans.getGenericTypeNested(propertyHolderType, propertyTopLevelName);

                yield newFilter(attributeName, propertyName, propertyTopLevelName, genericType, propertyHolderType, true);
            }
        };
    }

}
