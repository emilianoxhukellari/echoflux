package echoflux.domain.core.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class PasswordMatchConstraintValidator implements ConstraintValidator<PasswordMatch, HasPassword> {

    @Override
    public boolean isValid(HasPassword value, ConstraintValidatorContext context) {
        return StringUtils.equals(value.getPassword(), value.getPasswordConfirmation());
    }

}
