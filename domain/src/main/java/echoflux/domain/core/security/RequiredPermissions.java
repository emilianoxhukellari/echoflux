package echoflux.domain.core.security;


import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Enables method-level authorization based on required permissions. All permissions
 * specified at {@link #value()} must be granted to access the annotated element.
 * </p>
 * <p>
 * When this annotation is applied on the class level, it will apply to all methods within the class.
 * </p>
 * <p>
 * The application might extend functionality to this annotation by authorizing the entire class itself.
 * </p>
 **/
@SuppressWarnings("SpringElInspection")
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@PreAuthorize("@permissionAccess.check(principal, #requiredPermissions)")
public @interface RequiredPermissions {

    PermissionType[] value() default {};

}
