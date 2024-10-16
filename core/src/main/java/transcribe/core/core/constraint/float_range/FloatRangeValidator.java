package transcribe.core.core.constraint.float_range;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FloatRangeValidator implements ConstraintValidator<FloatRange, Float> {

    private float min;
    private float max;

    @Override
    public void initialize(FloatRange constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Float value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value >= min && value <= max;
    }

}
