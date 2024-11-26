package transcribe.domain.transcript.transcript_part_text.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.domain.transcript.transcript_part_text.data.TranscriptPartTextType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTranscriptPartTextCommand {

    @NotNull
    private Long transcriptPartId;

    @NotNull
    private String content;

    @NotNull
    @Min(0)
    private Integer version;

    @NotNull
    private TranscriptPartTextType type;

}
