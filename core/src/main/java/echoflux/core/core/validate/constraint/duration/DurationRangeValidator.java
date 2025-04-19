package echoflux.core.core.validate.constraint.duration;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;

public class DurationRangeValidator implements ConstraintValidator<DurationRange, Duration> {

    private long minMillis;
    private long maxMillis;

    @Override
    public void initialize(DurationRange constraintAnnotation) {
        minMillis = constraintAnnotation.minMillis();
        maxMillis = constraintAnnotation.maxMillis();
    }

    @Override
    public boolean isValid(Duration value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        long millis = value.toMillis();

        return millis >= minMillis && millis <= maxMillis;
    }

}
