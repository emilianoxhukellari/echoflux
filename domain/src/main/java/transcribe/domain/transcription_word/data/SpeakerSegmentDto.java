package transcribe.domain.transcription_word.data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.core.word.common.SpeakerSegmentInfo;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpeakerSegmentDto implements SpeakerSegmentInfo<WordDto> {

    @NotBlank
    private String speakerName;

    @Min(0)
    @NotNull
    private Long startOffsetMillis;

    @Min(0)
    @NotNull
    private Long endOffsetMillis;

    @NotNull
    @Builder.Default
    private List<WordDto> words = List.of();

}
