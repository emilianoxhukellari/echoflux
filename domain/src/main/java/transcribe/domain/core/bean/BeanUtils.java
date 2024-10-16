package transcribe.domain.core.bean;

import jakarta.persistence.Id;
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
        Objects.requireNonNull(beanType, "Bean type must not be null");
        Validate.notBlank(fieldName, "Field name must not be blank");

        var field = FieldUtils.getField(beanType, fieldName, true);

        return REQUIRED_FIELD_ANNOTATIONS.stream()
                .anyMatch(field::isAnnotationPresent);
    }

    public static <T> Optional<Field> findIdField(Class<T> beanType) {
        var fields = FieldUtils.getFieldsListWithAnnotation(beanType, Id.class);
        Validate.inclusiveBetween(0, 1, fields.size(), "Only one field can be annotated with @Id");

        return fields.stream().findFirst();
    }

    public static <T> Field getIdField(Class<T> beanType) {
        return findIdField(beanType)
                .orElseThrow(() -> new IllegalArgumentException("Bean does not have an @Id field"));
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

    public static <T> String getDisplayName(Class<T> beanType) {
        return beanType.getSimpleName().replaceAll("([a-z])([A-Z])", "$1 $2");
    }

}
