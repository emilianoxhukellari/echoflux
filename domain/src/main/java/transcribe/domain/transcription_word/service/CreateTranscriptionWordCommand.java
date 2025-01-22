package transcribe.domain.transcription_word.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.domain.transcription_speaker.data.TranscriptionSpeakerEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTranscriptionWordCommand {

    @NotNull
    private Long transcriptionId;

    @NotNull
    private TranscriptionSpeakerEntity transcriptionSpeakerEntity;

    @NotNull
    @Min(0)
    private Integer sequence;

    @NotNull
    @Min(0)
    private Long startOffsetMillis;

    @NotNull
    @Min(0)
    private Long endOffsetMillis;

    @NotBlank
    private String content;

}
