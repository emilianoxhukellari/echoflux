package transcribe.core.core.validate.constraint.duration_range;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DurationValidator implements ConstraintValidator<DurationRange, java.time.Duration> {

    private long minMillis;
    private long maxMillis;

    @Override
    public void initialize(DurationRange constraintAnnotation) {
        minMillis = constraintAnnotation.minMillis();
        maxMillis = constraintAnnotation.maxMillis();
    }

    @Override
    public boolean isValid(java.time.Duration value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        long millis = value.toMillis();

        return millis >= minMillis && millis <= maxMillis;
    }

}
