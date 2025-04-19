package echoflux.annotation.projection;

import echoflux.annotation.core.ObjectConvertable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * An interface annotated with {@link ProjectionInterface} is used to support class-based DTO projections in JPA.
 * </p>
 * <p>
 * Such an interface must implement {@link ObjectConvertable} as this is required to convert the interface data to the source DTO.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectionInterface {

    /**
     * The bean type for which the projection interface should be used.
     */
    Class<?> forBeanType();

}