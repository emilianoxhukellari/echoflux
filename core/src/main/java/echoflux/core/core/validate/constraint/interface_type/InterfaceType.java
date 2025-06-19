package echoflux.core.core.validate.constraint.interface_type;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = InterfaceTypeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface InterfaceType {

    String message() default "Class must be an interface";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
