package echoflux.template.renderer;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RenderTemplateFromFileCommand {

    @NotBlank
    private String templateName;
    private Map<String, Object> dataModel;

}
