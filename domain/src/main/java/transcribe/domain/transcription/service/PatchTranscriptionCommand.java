package transcribe.domain.transcription.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.domain.transcription.data.TranscriptionStatus;

import java.net.URI;

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

    @Min(0)
    private Long lengthMillis;

    private String error;

}
