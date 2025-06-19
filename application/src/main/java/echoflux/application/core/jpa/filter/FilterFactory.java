package echoflux.application.core.jpa.filter;

import echoflux.application.core.jpa.core.JpaPropertyType;
import echoflux.application.core.jpa.filter.impl.BooleanJpaFilter;
import echoflux.application.core.jpa.filter.impl.DurationJpaFilter;
import echoflux.application.core.jpa.filter.impl.InstantJpaFilter;
import echoflux.application.core.jpa.filter.impl.NumberJpaFilter;
import echoflux.application.core.jpa.filter.impl.EnumJpaFilter;
import echoflux.application.core.jpa.filter.impl.LocalDateJpaFilter;
import echoflux.application.core.jpa.filter.impl.TextJpaFilter;
import org.springframework.data.util.TypeInformation;

import java.util.Objects;

public final class FilterFactory {

    public static <E> JpaFilter<E> newFilter(String propertyName,
                                             String propertyTopLevelName,
                                             TypeInformation<?> propertyTypeInformation,
                                             boolean asCollection) {
        Objects.requireNonNull(propertyName, "propertyName");
        Objects.requireNonNull(propertyTopLevelName, "propertyTopLevelName");
        Objects.requireNonNull(propertyTypeInformation, "propertyTypeInformation");

        var type = propertyTypeInformation.getType();
        var supportedType = JpaPropertyType.ofBeanType(type);

        return switch (supportedType) {
            case STRING, URI, JSON, ZONE_ID -> new TextJpaFilter<>(propertyName, asCollection);
            case BOOLEAN -> new BooleanJpaFilter<>(propertyName, asCollection);
            case ENUM -> new EnumJpaFilter<>(propertyName, type, asCollection);
            case LOCAL_DATE, LOCAL_DATE_TIME -> new LocalDateJpaFilter<>(propertyName, asCollection);
            case INSTANT -> new InstantJpaFilter<>(propertyName, asCollection);
            case DOUBLE, FLOAT, LONG, INTEGER -> new NumberJpaFilter<>(propertyName, asCollection);
            case DURATION -> new DurationJpaFilter<>(propertyName, asCollection);
            case COLLECTION -> {
                if (asCollection) {
                    throw new UnsupportedOperationException("Collection filter does not support nested collections");
                }
                var genericType = propertyTypeInformation.getRequiredComponentType();

                yield newFilter(propertyName, propertyTopLevelName, genericType, true);
            }
        };
    }

}
