package transcribe.domain.template.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.template.data.TemplateEntity;

@Validated
public interface TemplateService {

    TemplateEntity getByName(@NotBlank String name);

    String render(@Valid @NotNull RenderTemplateCommand command);

}
