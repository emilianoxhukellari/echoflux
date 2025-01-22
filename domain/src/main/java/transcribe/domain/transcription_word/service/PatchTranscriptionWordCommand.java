package transcribe.domain.transcription_word.service;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchTranscriptionWordCommand {

    @NotNull
    private Long id;
    private Long transcriptionSpeakerId;
    private Integer sequence;
    private Long startOffsetMillis;
    private Long endOffsetMillis;
    private String content;

}
