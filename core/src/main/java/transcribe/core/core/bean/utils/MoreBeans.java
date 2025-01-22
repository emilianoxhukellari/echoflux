package transcribe.core.core.bean.utils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import transcribe.annotation.core.AttributeOverride;
import transcribe.annotation.core.ParentProperty;
import transcribe.core.core.annotation.Required;
import transcribe.core.core.bean.FieldProperty;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
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

    public static <T> List<String> getFieldNames(Class<T> beanType) {
        Objects.requireNonNull(beanType, "Bean type must not be null");

        return FieldUtils.getAllFieldsList(beanType).stream()
                .map(Field::getName)
                .toList();
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

    public static <T> List<String> getAttributeNamesNested(Class<T> beanType) {
        return getFieldPropertiesNested(beanType)
                .stream()
                .map(FieldProperty::getAttributeName)
                .toList();
    }

    public static <T> List<FieldProperty> getFieldPropertiesNested(Class<T> beanType) {
        return getFieldPropertiesNested(beanType, new HashSet<>());
    }

    public static List<FieldProperty> getFieldProperties(Class<?> beanType) {
        Objects.requireNonNull(beanType, "Bean type must not be null");

        var fieldResults = new ArrayList<FieldProperty>();

        for (var field : FieldUtils.getAllFieldsList(beanType)) {
            var attributeOverride = field.getAnnotation(AttributeOverride.class);
            var attributeName = attributeOverride != null ? attributeOverride.value() : field.getName();
            var isParentProperty = field.isAnnotationPresent(ParentProperty.class);

            fieldResults.add(
                    FieldProperty.builder()
                            .field(field)
                            .name(field.getName())
                            .attributeName(attributeName)
                            .build()
            );
        }

        return fieldResults;
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
            var attributeOverride = field.getAnnotation(AttributeOverride.class);
            var attributeName = attributeOverride != null ? attributeOverride.value() : field.getName();

            fieldResults.add(
                    FieldProperty.builder()
                            .field(field)
                            .parentField(null)
                            .name(field.getName())
                            .attributeName(attributeName)
                            .build()
            );
            if (field.isAnnotationPresent(ParentProperty.class)) {
                var parentType = field.getType();
                var children = getFieldPropertiesNested(parentType, visitedTypes);

                for (var child : children) {
                    fieldResults.add(
                            FieldProperty.builder()
                                    .field(child.getField())
                                    .parentField(field)
                                    .name("%s.%s".formatted(field.getName(), child.getName()))
                                    .attributeName("%s.%s".formatted(attributeName, child.getAttributeName()))
                                    .build()
                    );
                }
            }
        }

        return fieldResults;
    }

}
