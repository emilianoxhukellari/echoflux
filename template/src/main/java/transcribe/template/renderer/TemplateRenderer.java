package transcribe.template.renderer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface TemplateRenderer {

    String render(@Valid @NotNull RenderTemplateFromFileCommand command);

    String render(@Valid @NotNull RenderTemplateFromStringCommand command);

}
