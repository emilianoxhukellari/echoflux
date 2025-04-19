package echoflux.annotation.projection;

import echoflux.annotation.core.ObjectConvertable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates a {@link ProjectionInterface} that implements {@link ObjectConvertable} for the annotated class.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectionInterfaceSupport {
}