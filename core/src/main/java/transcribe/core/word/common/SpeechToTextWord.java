package transcribe.core.word.common;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String content;

    @Min(0)
    private long startOffsetMillis;

    @Min(0)
    private long endOffsetMillis;

    public SpeechToTextWord shiftOffsets(long offsetMillis) {
        this.startOffsetMillis += offsetMillis;
        this.endOffsetMillis += offsetMillis;

        return this;
    }

}