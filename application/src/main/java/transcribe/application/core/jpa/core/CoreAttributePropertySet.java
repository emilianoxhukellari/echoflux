package transcribe.application.core.jpa.core;

import com.vaadin.flow.data.binder.BeanPropertySet;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.PropertySet;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.lang.Nullable;
import transcribe.domain.audit.data.AuditEntity;
import transcribe.domain.core.bean.BeanUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class CoreAttributePropertySet<T> implements PropertySet<T> {

    private final static List<String> AUDIT_ENTITY_FIELD_NAMES = BeanUtils.getFieldNames(AuditEntity.class);

    private final List<String> excludedProperties;
    private final PropertySet<T> internalPropertySet;
    private final List<String> sortedFieldNames;
    @Nullable
    private final Field idField;

    public CoreAttributePropertySet(Class<T> beanType, List<String> excludedProperties) {
        Objects.requireNonNull(beanType, "Bean type must not be null");
        this.excludedProperties = ListUtils.emptyIfNull(excludedProperties);
        this.internalPropertySet = BeanPropertySet.get(beanType);
        this.sortedFieldNames = FieldUtils.getAllFieldsList(beanType)
                .stream().map(Field::getName)
                .toList();
        this.idField = BeanUtils.findIdField(beanType).orElse(null);
    }

    public static <T> CoreAttributePropertySet<T> getExcluding(Class<T> beanType, List<String> excludedProperties) {
        return new CoreAttributePropertySet<>(beanType, excludedProperties);
    }

    @Override
    public Stream<PropertyDefinition<T, ?>> getProperties() {
        return internalPropertySet.getProperties()
                .filter(p -> notIdOrAuditPropertyName(p.getName()))
                .sorted((p1, p2) -> Integer.compare(
                        sortedFieldNames.indexOf(p1.getName()),
                        sortedFieldNames.indexOf(p2.getName())
                ));
    }

    @Override
    public Optional<PropertyDefinition<T, ?>> getProperty(String name) {
        return internalPropertySet.getProperty(name)
                .filter(p -> notIdOrAuditPropertyName(p.getName()));
    }

    public List<PropertyDefinition<T, ?>> getPropertiesAsList() {
        return getProperties().toList();
    }

    public String[] getPropertyNamesAsArray() {
        return getProperties().map(PropertyDefinition::getName).toArray(String[]::new);
    }

    private boolean notIdOrAuditPropertyName(String propertyName) {
        var isNotIdField = idField == null || !idField.getName().equals(propertyName);
        var isNotAuditField = !AUDIT_ENTITY_FIELD_NAMES.contains(propertyName);

        return isNotIdField && isNotAuditField && !excludedProperties.contains(propertyName);
    }

}
