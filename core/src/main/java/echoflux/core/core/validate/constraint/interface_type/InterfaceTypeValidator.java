package echoflux.core.core.validate.constraint.interface_type;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InterfaceTypeValidator implements ConstraintValidator<InterfaceType, Class<?>> {

    @Override
    public boolean isValid(Class<?> value, ConstraintValidatorContext context) {
        return value == null || value.isInterface();
    }

}
