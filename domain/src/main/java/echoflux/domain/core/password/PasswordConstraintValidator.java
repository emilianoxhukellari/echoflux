package echoflux.domain.core.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.Rule;

import java.util.List;

public class PasswordConstraintValidator implements ConstraintValidator<Password, String> {

    private final static List<Rule> RULES = List.of(
            new LengthRule(8, 30),
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            new CharacterRule(EnglishCharacterData.Digit, 1),
            new CharacterRule(EnglishCharacterData.Special, 1)
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        var validator = new PasswordValidator(RULES);

        var result = validator.validate(new PasswordData(value));
        if (result.isValid()) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                String.join(" ", validator.getMessages(result))
        ).addConstraintViolation();

        return false;
    }

}
