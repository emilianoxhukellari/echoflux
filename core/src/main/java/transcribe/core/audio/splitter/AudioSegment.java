package transcribe.core.audio.splitter;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AudioSegment {

    @Min(0)
    private long startMillis;

    @Min(0)
    private long endMillis;

    public long getDurationMillis() {
        return endMillis - startMillis;
    }

}
