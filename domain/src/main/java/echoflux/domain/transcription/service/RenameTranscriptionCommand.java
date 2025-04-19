package echoflux.domain.transcription.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RenameTranscriptionCommand {

    @NotNull
    private Long id;

    @NotBlank(message = "Name must not be blank")
    @Size(min = 1, max = 1024, message = "Name must be between 1 and 255 characters")
    private String name;

}
