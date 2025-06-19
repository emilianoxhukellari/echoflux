package echoflux.core.core.validate.constraint.duration;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PositiveDurationValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveDuration {

    String message() default "Duration must be positive";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
