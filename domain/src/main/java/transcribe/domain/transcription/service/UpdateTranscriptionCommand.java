package transcribe.domain.transcription.service;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import transcribe.domain.transcription.data.TranscriptionStatus;

import java.net.URI;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UpdateTranscriptionCommand {

    @NotNull
    private Long id;

    private TranscriptionStatus status;

    private URI cloudUri;

    private String name;

    private String transcript;

    private Long lengthMillis;

    private String error;

}
