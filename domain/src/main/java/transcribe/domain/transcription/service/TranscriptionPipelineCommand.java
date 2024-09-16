package transcribe.domain.transcription.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.transcribe.common.Language;
import transcribe.domain.transcription.data.MediaOrigin;

import java.net.URI;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptionPipelineCommand {

    @NotBlank
    private String name;

    @NotNull
    private Language language;

    @NotNull
    private Long applicationUserId;

    @NotNull
    private URI mediaUri;

    @NotNull
    private MediaOrigin mediaOrigin;

}
