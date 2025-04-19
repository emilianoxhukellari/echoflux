package echoflux.core.core.validate.formatter.impl;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import echoflux.core.core.validate.formatter.StrictValidator;

@Component
@RequiredArgsConstructor
public class StrictValidatorImpl implements StrictValidator {

    private final SmartValidator validator;

    @Override
    public <T> T validate(T object) {
        var errors = new BeanPropertyBindingResult(object, object.getClass().getName());
        validator.validate(object, errors);

        if (errors.hasErrors()) {
            throw new ValidationException(errors.getAllErrors().toString());
        }

        return object;
    }

}
