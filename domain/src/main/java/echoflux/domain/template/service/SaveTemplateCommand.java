package echoflux.domain.template.service;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaveTemplateCommand {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String content;

}
