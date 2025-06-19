package echoflux.domain.template.service;

import echoflux.domain.template.data.TemplateEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface TemplateService {

    String render(@Valid @NotNull RenderTemplateCommand command);

    TemplateEntity save(SaveTemplateCommand command);

    void deleteById(@NotNull Long id);

}
