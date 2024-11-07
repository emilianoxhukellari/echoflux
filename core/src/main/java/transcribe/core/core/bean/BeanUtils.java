package transcribe.core.core.bean;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class BeanUtils {

    private static final List<Class<? extends Annotation>> REQUIRED_FIELD_ANNOTATIONS;

    static {
        REQUIRED_FIELD_ANNOTATIONS = List.of(NotNull.class, NotBlank.class, NotEmpty.class);
    }

    public static <T> Class<?> getGenericType(Class<T> beanType, String fieldName) {
        var field = FieldUtils.getField(beanType, fieldName, true);

        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
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

    public static <T> boolean isFieldRequired(Class<T> beanType, String fieldName) {
        var field = getFieldRequired(beanType, fieldName);

        return REQUIRED_FIELD_ANNOTATIONS.stream()
                .anyMatch(field::isAnnotationPresent);
    }

    public static <T extends Annotation> Optional<T> findAnnotation(Class<?> beanType,
                                                                    String fieldName,
                                                                    Class<T> annotationType) {
        Objects.requireNonNull(annotationType, "Annotation type must not be null");

        var field = getFieldRequired(beanType, fieldName);
        var annotation = field.getAnnotation(annotationType);

        return Optional.ofNullable(annotation);
    }

    public static <T> boolean isAnnotationPresent(Class<T> beanType,
                                                  String fieldName,
                                                  Class<? extends Annotation> annotationType) {
        return findAnnotation(beanType, fieldName, annotationType).isPresent();
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

    public static Field getFieldRequired(Class<?> beanType, String fieldName) {
        Objects.requireNonNull(beanType, "Bean type must not be null");
        Validate.notBlank(fieldName, "Field name must not be blank");

        var field = FieldUtils.getField(beanType, fieldName, true);

        return Objects.requireNonNull(field, "Field not found: " + fieldName);
    }

    public static <T> String getDisplayName(Class<T> beanType) {
        return beanType.getSimpleName().replaceAll("([a-z])([A-Z])", "$1 $2");
    }

}
