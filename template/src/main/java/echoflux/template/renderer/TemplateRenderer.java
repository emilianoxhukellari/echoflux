package echoflux.template.renderer;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Validated
public interface TemplateRenderer {

    String renderFromFile(@NotBlank String templateName,  Map<String, Object> dataModel);

    String renderFromString(@NotBlank String template, Map<String, Object> dataModel);

}
