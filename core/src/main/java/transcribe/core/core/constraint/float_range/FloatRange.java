package transcribe.core.core.constraint.float_range;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FloatRangeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface FloatRange {

    String message() default "Float value is out of range";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    float min() default Float.MIN_VALUE;

    float max() default Float.MAX_VALUE;

}
