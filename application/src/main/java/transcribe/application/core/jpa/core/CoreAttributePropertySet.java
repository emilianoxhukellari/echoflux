package transcribe.application.core.jpa.core;

import com.vaadin.flow.data.binder.BeanPropertySet;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.PropertyFilterDefinition;
import com.vaadin.flow.data.binder.PropertySet;
import jakarta.persistence.Id;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import transcribe.core.core.bean.FieldProperty;
import transcribe.domain.audit.data.AuditEntity;
import transcribe.core.core.bean.BeanUtils;
import transcribe.core.core.annotation.ParentProperty;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class CoreAttributePropertySet<T> implements PropertySet<T> {

    private final static List<String> AUDIT_ENTITY_FIELD_NAMES = BeanUtils.getFieldNames(AuditEntity.class);

    private final List<String> excludedProperties;
    private final PropertySet<T> internalPropertySet;
    private final List<FieldProperty> fieldProperties;
    private final Map<String, Integer> fieldNameIndexMap;
    private final Class<T> beanType;
    private final List<FieldProperty> idFieldProperties;

    @SneakyThrows
    public CoreAttributePropertySet(Class<T> beanType, List<String> excludedProperties) {
        this.beanType = Objects.requireNonNull(beanType, "Bean type must not be null");
        this.excludedProperties = ListUtils.emptyIfNull(excludedProperties);
        this.internalPropertySet = BeanPropertySet.get(
                beanType,
                true,
                PropertyFilterDefinition.getDefaultFilter()
        );

        this.fieldProperties = BeanUtils.getFieldPropertiesNested(beanType);
        this.fieldNameIndexMap = IntStream.range(0, fieldProperties.size())
                .boxed()
                .collect(Collectors.toMap(i -> fieldProperties.get(i).getName(), Function.identity()));
        this.idFieldProperties = fieldProperties.stream()
                .filter(fp -> fp.getField().isAnnotationPresent(Id.class))
                .toList();
    }

    public static <T> CoreAttributePropertySet<T> getExcluding(Class<T> beanType, List<String> excludedProperties) {
        return new CoreAttributePropertySet<>(beanType, excludedProperties);
    }

    @Override
    public Stream<PropertyDefinition<T, ?>> getProperties() {
        return internalPropertySet.getProperties()
                .filter(this::isCoreAttribute)
                .sorted((p1, p2) -> Integer.compare(
                        fieldNameIndexMap.get(p1.getName()),
                        fieldNameIndexMap.get(p2.getName())
                ));
    }

    @Override
    public Optional<PropertyDefinition<T, ?>> getProperty(String name) {
        return internalPropertySet.getProperty(name)
                .filter(this::isCoreAttribute);
    }

    public List<PropertyDefinition<T, ?>> getPropertiesAsList() {
        return getProperties().toList();
    }

    public String[] getPropertyNamesAsArray() {
        return getProperties().map(PropertyDefinition::getName).toArray(String[]::new);
    }

    private boolean isCoreAttribute(PropertyDefinition<T, ?> propertyDefinition) {
        return isNotIdField(propertyDefinition)
                && isNotAuditField(propertyDefinition)
                && !excludedProperties.contains(propertyDefinition.getName())
                && !BeanUtils.isAnnotationPresentNested(beanType, propertyDefinition.getName(), ParentProperty.class);
    }

    private boolean isNotIdField(PropertyDefinition<T, ?> propertyDefinition) {
        return idFieldProperties.stream()
                .noneMatch(fp -> fp.getName().equals(propertyDefinition.getName()));
    }

    private boolean isNotAuditField(PropertyDefinition<T, ?> propertyDefinition) {
        return AUDIT_ENTITY_FIELD_NAMES.stream()
                .noneMatch(a ->
                        StringUtils.equals(a, propertyDefinition.getName())
                                || StringUtils.endsWith(propertyDefinition.getName(), "." + a)
                );
    }

}
