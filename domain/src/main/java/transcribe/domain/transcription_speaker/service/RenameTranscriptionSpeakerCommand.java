package transcribe.domain.transcription_speaker.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RenameTranscriptionSpeakerCommand {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

}
