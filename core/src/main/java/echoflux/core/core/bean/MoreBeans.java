package echoflux.core.core.bean;

import echoflux.core.core.validate.guard.Guard;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.annotation.Annotation;
import java.util.Optional;

public final class MoreBeans {

    public static <T> T invokeBuilderOrNoArgsConstructor(Class<T> beanType) {
        Guard.notNull(beanType, "Bean type must not be null");

        return MethodUtils.getAccessibleMethod(beanType, "builder") != null
                ? invokeEmptyBuilder(beanType)
                : invokeNoArgsConstructor(beanType);
    }

    @SneakyThrows
    public static <T> T invokeNoArgsConstructor(Class<T> beanType) {
        Guard.notNull(beanType, "Bean type must not be null");

        return ConstructorUtils.invokeConstructor(beanType);
    }

    @SneakyThrows
    public static <T> T invokeEmptyBuilder(Class<T> beanType) {
        Guard.notNull(beanType, "Bean type must not be null");

        var builder = MethodUtils.invokeStaticMethod(beanType, "builder");
        var buildMethod = MethodUtils.getAccessibleMethod(builder.getClass(), "build");

        return beanType.cast(buildMethod.invoke(builder));
    }


    public static <T extends Annotation> Optional<T> findAnnotation(Class<?> beanType,
                                                                    String nestedFieldName,
                                                                    Class<T> annotationType) {
        Guard.notNull(annotationType, "Annotation type must not be null");
        Guard.notNull(beanType, "Bean type must not be null");
        Guard.notNull(nestedFieldName, "Nested field name must not be null");

        try {
            var field = beanType.getDeclaredField(nestedFieldName);
            var annotation = field.getAnnotation(annotationType);

            return Optional.ofNullable(annotation);
        } catch (NoSuchFieldException e) {
            return Optional.empty();
        }
    }

    public static <T> String getDisplayName(Class<T> beanType) {
        return beanType.getSimpleName().replaceAll("([a-z])([A-Z])", "$1 $2");
    }

}
