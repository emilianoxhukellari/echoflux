package transcribe.core.core.validate.constraint.duration;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;

public class PositiveOrZeroDurationValidator implements ConstraintValidator<PositiveOrZeroDuration, Duration> {

    @Override
    public boolean isValid(Duration duration, ConstraintValidatorContext constraintValidatorContext) {
        if (duration == null) {
            return true;
        }

        return duration.isZero() || duration.isPositive();
    }

}
