package transcribe.core.audio.splitter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AudioSegment {

    @NotNull
    @Min(0)
    private Long startMillis;

    @NotNull
    @Min(0)
    private Long endMillis;

    public Long getDurationMillis() {
        return endMillis - startMillis;
    }

}
