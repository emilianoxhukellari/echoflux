package transcribe.core.core.validate.constraint.duration;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DurationRangeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DurationRange {

    String message() default "Duration value is out of range";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    long minMillis() default Long.MIN_VALUE;

    long maxMillis() default Long.MAX_VALUE;

}
