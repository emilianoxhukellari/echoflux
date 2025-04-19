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
public class RenderTemplateFromStringCommand {

    @NotBlank
    private String template;
    private Map<String, Object> dataModel;

}
