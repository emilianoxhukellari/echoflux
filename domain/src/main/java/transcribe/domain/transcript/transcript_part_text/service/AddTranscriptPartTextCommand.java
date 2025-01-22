package transcribe.domain.transcript.transcript_part_text.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.domain.transcript.transcript_part_text.data.TranscriptPartTextType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddTranscriptPartTextCommand {

    @NotNull
    private Long transcriptPartId;

    @NotBlank
    @Size(max = 10000)
    private String content;

    @NotNull
    private TranscriptPartTextType type;

}
