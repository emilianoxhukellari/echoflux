package transcribe.domain.template.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RenderTemplateCommand {

    @NotBlank
    private String name;

    @NotNull
    private Map<@NotBlank String, @NotNull Object> dataModel;

}
