package echoflux.domain.core.security;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * An endpoint is a specialized component that an {@link ApplicationUser} will interact with. An endpoint provides
 * additional authorization and security features to ensure that only authorized users can access its methods.
 * </p>
 * <p>
 *  Authorization is done using Spring method security annotations. In addition {@link RequiredPermissions} is supported.
 * </p>
 * <p>
 * This annotation serves as a specialization of {@link Component @Component},
 * allowing for implementation classes to be autodetected through classpath scanning.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Endpoint {

    @AliasFor(annotation = Component.class)
    String value() default "";

}
