package transcribe.core.transcribe.common;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SpeechToTextWord {

    @NotNull
    private String content;

    @NotNull
    private String speakerName;

    @Min(0)
    @NotNull
    private Long startOffsetMillis;

    @Min(0)
    @NotNull
    private Long endOffsetMillis;

}