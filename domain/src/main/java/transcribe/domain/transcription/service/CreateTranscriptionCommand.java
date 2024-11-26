package transcribe.domain.transcription.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.transcribe.common.Language;

import java.net.URI;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTranscriptionCommand {

    @NotBlank
    private String name;

    @NotNull
    private Language language;

    @NotNull
    private Long applicationUserId;

    @NotNull
    private Boolean enhanced;

    @NotNull
    private URI sourceUri;

}
