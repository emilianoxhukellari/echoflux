package echoflux.domain.transcription.service;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import echoflux.core.core.validate.constraint.duration.PositiveOrZeroDuration;
import echoflux.domain.transcription.data.TranscriptionStatus;

import java.net.URI;
import java.time.Duration;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchTranscriptionCommand {

    @NotNull
    private Long id;

    private TranscriptionStatus status;

    private URI cloudUri;

    private String name;

    @PositiveOrZeroDuration
    private Duration length;

    private String error;

}
