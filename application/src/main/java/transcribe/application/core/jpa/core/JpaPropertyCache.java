package transcribe.application.core.jpa.core;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vaadin.flow.data.binder.BeanPropertySet;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.PropertyFilterDefinition;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import transcribe.application.core.jpa.dto.JpaDtoConfiguration;
import transcribe.annotation.core.ParentProperty;
import transcribe.core.core.bean.utils.MoreBeans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class JpaPropertyCache {

    private static final LoadingCache<Class<?>, Value<?>> CACHE = CacheBuilder.newBuilder()
            .build(CacheLoader.from(JpaPropertyCache::computeValue));

    public static <DTO> List<JpaPropertyDefinition<DTO, ?>> getProperties(Class<DTO> beanType) {
        return getOrComputeValue(beanType).getProperties();
    }

    public static <DTO> JpaPropertyDefinition<DTO, ?> getIdProperty(Class<DTO> beanType) {
        return getProperties(beanType)
                .stream()
                .filter(p -> p.getPropertyTypes().contains(JpaPropertyType.ID))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No ID property found. This should not happen."));
    }

    public static <DTO> JpaPropertyDefinition<DTO, ?> getVersionProperty(Class<DTO> beanType) {
        return getProperties(beanType)
                .stream()
                .filter(p -> p.getPropertyTypes().contains(JpaPropertyType.VERSION))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No version property found."));
    }

    public static <DTO> List<JpaPropertyDefinition<DTO, ?>> getAuditProperties(Class<DTO> beanType) {
        return getProperties(beanType)
                .stream()
                .filter(p -> p.getPropertyTypes().contains(JpaPropertyType.AUDIT))
                .toList();
    }

    public static <DTO> List<JpaPropertyDefinition<DTO, ?>> getCoreProperties(Class<DTO> beanType) {
        return getCorePropertiesExcluding(beanType, List.of());
    }

    public static <DTO> List<JpaPropertyDefinition<DTO, ?>> getCorePropertiesExcluding(Class<DTO> beanType,
                                                                                       List<String> excludedProperties) {
        return getProperties(beanType)
                .stream()
                .filter(p ->
                        p.getPropertyTypes().contains(JpaPropertyType.CORE)
                                && !excludedProperties.contains(p.getName())
                )
                .toList();
    }

    public static <DTO> Optional<JpaPropertyDefinition<DTO, ?>> findPropertyByName(Class<DTO> beanType, String name) {
        return getOrComputeValue(beanType).findByName(name);
    }

    @SuppressWarnings("unchecked")
    private static <DTO> Value<DTO> getOrComputeValue(Class<DTO> beanType) {
        return (Value<DTO>) CACHE.getUnchecked(beanType);
    }

    private static <DTO> Value<DTO> computeValue(Class<DTO> beanType) {
        var jpaDtoConfiguration = JpaDtoConfiguration.ofBeanType(beanType);
        var propertySet = BeanPropertySet.get(
                beanType,
                true,
                PropertyFilterDefinition.getDefaultFilter()
        );
        var fieldProperties = MoreBeans.getFieldPropertiesNested(beanType);

        var properties = new ArrayList<JpaPropertyDefinition<DTO, ?>>();
        for (var fieldProperty : fieldProperties) {
            var propertyDefinition = propertySet.getProperty(fieldProperty.getName())
                    .orElseThrow();
            var types = resolveTypes(beanType, propertyDefinition, jpaDtoConfiguration);

            properties.add(new JpaPropertyDefinition<>(propertyDefinition, fieldProperty, types));
        }

        Validate.isTrue(
                properties.stream()
                        .filter(p -> p.getPropertyTypes().contains(JpaPropertyType.ID))
                        .count() == 1,
                "There must be exactly one ID property per JPA DTO"
        );

        return new Value<>(Collections.unmodifiableList(properties));
    }

    private static <DTO> Set<JpaPropertyType> resolveTypes(Class<DTO> beanType,
                                                           PropertyDefinition<DTO, ?> propertyDefinition,
                                                           JpaDtoConfiguration configuration) {
        var types = new HashSet<JpaPropertyType>();

        if (isIdProperty(propertyDefinition, configuration)) {
            types.add(JpaPropertyType.ID);
        }
        if (isVersionProperty(propertyDefinition, configuration)) {
            types.add(JpaPropertyType.VERSION);
        }
        if (isAuditProperty(propertyDefinition, configuration)) {
            types.add(JpaPropertyType.AUDIT);
        }
        if (isHiddenProperty(propertyDefinition, configuration)) {
            types.add(JpaPropertyType.HIDDEN);
        }
        if (isParentProperty(beanType, propertyDefinition)) {
            types.add(JpaPropertyType.PARENT);
        }

        if (types.contains(JpaPropertyType.ID)) {
            if (types.contains(JpaPropertyType.VERSION)) {
                throw new IllegalArgumentException("An ID property cannot be a version property");
            }
            if (types.contains(JpaPropertyType.AUDIT)) {
                throw new IllegalArgumentException("An ID property cannot be an audit property");
            }
            if (types.contains(JpaPropertyType.PARENT)) {
                throw new IllegalArgumentException("An ID property cannot be a parent property");
            }
        }

        if (types.contains(JpaPropertyType.AUDIT)) {
            if (types.contains(JpaPropertyType.ID)) {
                throw new IllegalArgumentException("An audit property cannot be an ID property");
            }
            if (types.contains((JpaPropertyType.VERSION))) {
                throw new IllegalArgumentException("An audit property cannot be a version property");
            }
            if (types.contains(JpaPropertyType.PARENT)) {
                throw new IllegalArgumentException("An audit property cannot be a parent property");
            }
        }

        if (types.contains(JpaPropertyType.VERSION)) {
            if (types.contains(JpaPropertyType.ID)) {
                throw new IllegalArgumentException("A version property cannot be an ID property");
            }
            if (types.contains(JpaPropertyType.AUDIT)) {
                throw new IllegalArgumentException("A version property cannot be an audit property");
            }
            if (types.contains(JpaPropertyType.PARENT)) {
                throw new IllegalArgumentException("A version property cannot be a parent property");
            }
        }

        if (types.contains(JpaPropertyType.PARENT)) {
            if (types.contains(JpaPropertyType.ID)) {
                throw new IllegalArgumentException("A parent property cannot be an ID property");
            }
            if (types.contains(JpaPropertyType.AUDIT)) {
                throw new IllegalArgumentException("A parent property cannot be an audit property");
            }
            if (types.contains(JpaPropertyType.VERSION)) {
                throw new IllegalArgumentException("A parent property cannot be a version property");
            }
        }

        if (types.isEmpty()) {
            types.add(JpaPropertyType.CORE);
        }

        return types;
    }

    private static <DTO> boolean isIdProperty(PropertyDefinition<DTO, ?> propertyDefinition,
                                              JpaDtoConfiguration jpaDtoConfiguration) {
        return StringUtils.equals(jpaDtoConfiguration.idFieldName(), propertyDefinition.getName());
    }

    private static <DTO> boolean isVersionProperty(PropertyDefinition<DTO, ?> propertyDefinition,
                                                   JpaDtoConfiguration jpaDtoConfiguration) {
        return StringUtils.equals(jpaDtoConfiguration.versionFieldName(), propertyDefinition.getName());
    }

    private static <DTO> boolean isAuditProperty(PropertyDefinition<DTO, ?> propertyDefinition,
                                                 JpaDtoConfiguration jpaDtoConfiguration) {
        return jpaDtoConfiguration.auditFields()
                .stream()
                .anyMatch(a -> StringUtils.equals(a, propertyDefinition.getName()));
    }

    private static <DTO> boolean isHiddenProperty(PropertyDefinition<DTO, ?> propertyDefinition,
                                                  JpaDtoConfiguration jpaDtoConfiguration) {
        return jpaDtoConfiguration.hiddenFields()
                .stream()
                .anyMatch(h ->
                        StringUtils.equals(h, propertyDefinition.getName())
                                || StringUtils.startsWith(propertyDefinition.getName(), h + ".")
                );
    }

    private static <DTO> boolean isParentProperty(Class<DTO> beanType, PropertyDefinition<DTO, ?> propertyDefinition) {
        return MoreBeans.isAnnotationPresentNested(beanType, propertyDefinition.getName(), ParentProperty.class);
    }

    private static class Value<DTO> {

        @Getter
        private final List<JpaPropertyDefinition<DTO, ?>> properties;
        private final Map<String, JpaPropertyDefinition<DTO, ?>> byName;

        public Value(List<JpaPropertyDefinition<DTO, ?>> properties) {
            this.properties = Objects.requireNonNull(properties, "Properties must not be null");
            this.byName = properties.stream()
                    .collect(Collectors.toMap(JpaPropertyDefinition::getName, Function.identity()));
        }

        public Optional<JpaPropertyDefinition<DTO, ?>> findByName(String name) {
            return Optional.ofNullable(byName.get(name));
        }

    }

}
