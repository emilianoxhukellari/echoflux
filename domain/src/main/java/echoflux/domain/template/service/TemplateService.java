package echoflux.domain.template.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface TemplateService {

    String render(@Valid @NotNull RenderTemplateCommand command);

    Long save(@Valid @NotNull SaveTemplateCommand command);

    void deleteById(@NotNull Long id);

}
