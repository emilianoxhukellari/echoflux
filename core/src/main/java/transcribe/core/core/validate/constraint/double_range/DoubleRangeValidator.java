package transcribe.core.core.validate.constraint.double_range;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DoubleRangeValidator implements ConstraintValidator<DoubleRange, Double> {

    private double min;
    private double max;

    @Override
    public void initialize(DoubleRange constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value >= min && value <= max;
    }

}
