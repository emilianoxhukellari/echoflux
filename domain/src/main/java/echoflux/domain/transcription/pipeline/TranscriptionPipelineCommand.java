package echoflux.domain.transcription.pipeline;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import echoflux.core.transcribe.common.Language;
import echoflux.domain.transcription.data.MediaOrigin;

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
    private Boolean enhanced;

    @NotNull
    private URI sourceUri;

    @NotNull
    private MediaOrigin mediaOrigin;

}
