package echoflux.core.core.bean;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import echoflux.annotation.core.AttributeOverride;
import echoflux.annotation.core.ParentProperty;
import echoflux.annotation.projection.AttributeProjectType;
import echoflux.core.core.annotation.Required;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class MoreBeans {

    private static final List<Class<? extends Annotation>> REQUIRED_FIELD_ANNOTATIONS;

    static {
        REQUIRED_FIELD_ANNOTATIONS = List.of(Required.class, NotNull.class, NotBlank.class, NotEmpty.class);
    }

    public static <T> Class<?> getGenericTypeNested(Class<T> beanType, String fieldName) {
        var field = getFieldRequiredNested(beanType, fieldName);

        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    @SneakyThrows
    public static <T> T invokeBuilderOrNoArgsConstructorNested(Class<T> beanType) {
        var instance = invokeBuilderOrNoArgsConstructor(beanType);

        for (var field : FieldUtils.getAllFieldsList(beanType)) {
            if (field.isAnnotationPresent(ParentProperty.class)) {
                var fieldType = field.getType();
                var fieldValue = invokeBuilderOrNoArgsConstructorNested(fieldType);

                FieldUtils.writeField(field, instance, fieldValue, true);
            }
        }

        return instance;
    }

    public static <T> T invokeBuilderOrNoArgsConstructor(Class<T> beanType) {
        Objects.requireNonNull(beanType, "Bean type must not be null");

        return MethodUtils.getAccessibleMethod(beanType, "builder") != null
                ? invokeEmptyBuilder(beanType)
                : invokeNoArgsConstructor(beanType);
    }

    @SneakyThrows
    public static <T> T invokeNoArgsConstructor(Class<T> beanType) {
        Objects.requireNonNull(beanType, "Bean type must not be null");

        return ConstructorUtils.invokeConstructor(beanType);
    }

    @SneakyThrows
    public static <T> T invokeEmptyBuilder(Class<T> beanType) {
        Objects.requireNonNull(beanType, "Bean type must not be null");

        var builder = MethodUtils.invokeStaticMethod(beanType, "builder");
        var buildMethod = MethodUtils.getAccessibleMethod(builder.getClass(), "build");

        return beanType.cast(buildMethod.invoke(builder));
    }

    public static <T> boolean isFieldRequiredNested(Class<T> beanType, String nestedFieldName) {
        var field = getFieldRequiredNested(beanType, nestedFieldName);

        return REQUIRED_FIELD_ANNOTATIONS.stream()
                .anyMatch(field::isAnnotationPresent);
    }

    public static <T extends Annotation> Optional<T> findAnnotationNested(Class<?> beanType,
                                                                          String nestedFieldName,
                                                                          Class<T> annotationType) {
        Objects.requireNonNull(annotationType, "Annotation type must not be null");

        var field = getFieldRequiredNested(beanType, nestedFieldName);
        var annotation = field.getAnnotation(annotationType);

        return Optional.ofNullable(annotation);
    }

    public static <T> boolean isAnnotationPresentNested(Class<T> beanType,
                                                        String nestedFieldName,
                                                        Class<? extends Annotation> annotationType) {
        return findAnnotationNested(beanType, nestedFieldName, annotationType).isPresent();
    }

    public static <T> Optional<Field> findOneFieldWithAnnotation(Class<T> beanType,
                                                                 Class<? extends Annotation> annotationType) {
        Objects.requireNonNull(annotationType, "Annotation type must not be null");
        Objects.requireNonNull(beanType, "Bean type must not be null");

        var fields = FieldUtils.getFieldsListWithAnnotation(beanType, annotationType);
        Validate.inclusiveBetween(0, 1, fields.size(),
                "Only one field can be annotated with %s", annotationType.getSimpleName());

        return fields.stream().findFirst();
    }

    public static <T> Field getSingleFieldWithAnnotation(Class<T> beanType,
                                                         Class<? extends Annotation> annotationType) {
        return findOneFieldWithAnnotation(beanType, annotationType)
                .orElseThrow(
                        () -> new IllegalArgumentException("Bean does not have a field annotated with %s"
                                .formatted(annotationType.getSimpleName()))
                );
    }

    @SneakyThrows
    public static <T> Object getFieldValue(T bean, Field field) {
        Objects.requireNonNull(bean, "Bean must not be null");
        Objects.requireNonNull(field, "Field must not be null");

        return FieldUtils.readField(field, bean, true);
    }

    @SneakyThrows
    public static <T> Object getFieldValue(T bean, String fieldName) {
        Objects.requireNonNull(bean, "Bean must not be null");
        Validate.notBlank(fieldName, "Field name must not be blank");

        return FieldUtils.readField(bean, fieldName, true);
    }

    public static Field getFieldRequired(Class<?> beanType, String fieldName) {
        Objects.requireNonNull(beanType, "Bean type must not be null");
        Validate.notBlank(fieldName, "Field name must not be blank");

        var field = FieldUtils.getField(beanType, fieldName, true);

        return Objects.requireNonNull(field, "Field not found: %s".formatted(fieldName));
    }

    public static Field getFieldRequiredNested(Class<?> beanType, String fieldName) {
        Objects.requireNonNull(beanType, "Bean type must not be null");
        Validate.notBlank(fieldName, "Field name must not be blank");

        var fieldParts = fieldName.split("\\.");
        var currentType = beanType;
        Field field = null;

        for (var part : fieldParts) {
            field = FieldUtils.getField(currentType, part, true);
            if (field == null) {
                throw new IllegalArgumentException("Field [%s] not found in [%s]".formatted(part, currentType.getSimpleName()));
            }
            currentType = field.getType();
        }

        return Objects.requireNonNull(field, "Field not found: %s".formatted(fieldName));
    }

    public static <T> String getDisplayName(Class<T> beanType) {
        return beanType.getSimpleName().replaceAll("([a-z])([A-Z])", "$1 $2");
    }

    public static <T> List<String> getDefaultProjectAttributeNamesNested(Class<T> beanType) {
        return getAttributesNested(beanType)
                .stream()
                .filter(a -> AttributeProjectType.DEFAULT.equals(a.projectType()))
                .map(Attribute::name)
                .toList();
    }

    public static <T> List<Attribute> getAttributesNested(Class<T> beanType) {
        Objects.requireNonNull(beanType, "beanType");

        if (beanType.isInterface()) {
            return getAttributesNestedByGetter(beanType, new HashSet<>());
        }

        return getFieldPropertiesNested(beanType)
                .stream()
                .map(FieldProperty::getAttribute)
                .toList();
    }

    @SneakyThrows({IntrospectionException.class})
    public static List<Attribute> getAttributesNestedByGetter(Class<?> beanType, Set<Class<?>> visitedTypes) {
        Objects.requireNonNull(beanType, "beanType");
        Objects.requireNonNull(visitedTypes, "visitedTypes");

        if (visitedTypes.contains(beanType)) {
            return List.of();
        }

        visitedTypes.add(beanType);

        var attributes = new ArrayList<Attribute>();
        var beanInfo = Introspector.getBeanInfo(beanType);

        for (var propertyDescriptor : beanInfo.getPropertyDescriptors()) {
            var readMethod = propertyDescriptor.getReadMethod();

            String attributeName;
            if (StringUtils.startsWithIgnoreCase(propertyDescriptor.getName(), "get")) {
                attributeName = StringUtils.removeStartIgnoreCase(propertyDescriptor.getName(), "get");
            } else if (StringUtils.startsWithIgnoreCase(propertyDescriptor.getName(), "is")) {
                attributeName = StringUtils.removeStartIgnoreCase(propertyDescriptor.getName(), "is");
            } else {
                attributeName = propertyDescriptor.getName();
            }

            var attributeBuilder = Attribute.builder()
                    .name(attributeName);

            var attributeOverride = readMethod.getAnnotation(AttributeOverride.class);
            if (attributeOverride != null) {
                attributeBuilder.projectType(attributeOverride.projectType());
            } else {
                attributeBuilder.projectType(AttributeProjectType.DEFAULT);
            }

            var attribute = attributeBuilder.build();
            attributes.add(attribute);

            if (readMethod.isAnnotationPresent(ParentProperty.class)) {
                var returnType = readMethod.getReturnType();
                var children = getAttributesNestedByGetter(returnType, visitedTypes);

                for (var child : children) {
                    var childAttributeName = "%s.%s".formatted(attributeName, child.name());
                    var childProjectType = resolveProjectType(attribute.projectType(), child.projectType());
                    var childAttribute = new Attribute(childAttributeName, childProjectType);

                    attributes.add(childAttribute);
                }
            }
        }

        return attributes;
    }

    public static <T> List<FieldProperty> getFieldPropertiesNested(Class<T> beanType) {
        return getFieldPropertiesNested(beanType, new HashSet<>());
    }

    private static List<FieldProperty> getFieldPropertiesNested(Class<?> beanType, Set<Class<?>> visitedTypes) {
        Objects.requireNonNull(beanType, "Bean type must not be null");
        Objects.requireNonNull(visitedTypes, "Visited types must not be null");

        if (visitedTypes.contains(beanType)) {
            return List.of();
        }
        visitedTypes.add(beanType);

        var fieldResults = new ArrayList<FieldProperty>();

        for (var field : FieldUtils.getAllFieldsList(beanType)) {
            var attributeBuilder = Attribute.builder();
            var attributeOverride = field.getAnnotation(AttributeOverride.class);

            if (attributeOverride != null) {
                var attributeName = StringUtils.firstNonBlank(attributeOverride.name(), field.getName());
                attributeBuilder.name(attributeName);
                attributeBuilder.projectType(attributeOverride.projectType());
            } else {
                attributeBuilder.name(field.getName());
                attributeBuilder.projectType(AttributeProjectType.DEFAULT);
            }
            var attribute = attributeBuilder.build();

            fieldResults.add(
                    FieldProperty.builder()
                            .field(field)
                            .parentField(null)
                            .name(field.getName())
                            .attribute(attribute)
                            .build()
            );
            if (field.isAnnotationPresent(ParentProperty.class)) {
                var parentType = field.getType();
                var children = getFieldPropertiesNested(parentType, visitedTypes);

                for (var child : children) {
                    var childAttributeName = "%s.%s".formatted(attribute.name(), child.getAttribute().name());
                    var childProjectType = resolveProjectType(attribute.projectType(), child.getAttribute().projectType());
                    var childAttribute = new Attribute(childAttributeName, childProjectType);

                    fieldResults.add(
                            FieldProperty.builder()
                                    .field(child.getField())
                                    .parentField(field)
                                    .name("%s.%s".formatted(field.getName(), child.getName()))
                                    .attribute(childAttribute)
                                    .build()
                    );
                }
            }
        }

        return fieldResults;
    }

    private static AttributeProjectType resolveProjectType(AttributeProjectType... attributeProjectTypes) {
        return Arrays.stream(attributeProjectTypes)
                .min(Comparator.comparing(AttributeProjectType::getOrder))
                .orElse(AttributeProjectType.DEFAULT);
    }

}
