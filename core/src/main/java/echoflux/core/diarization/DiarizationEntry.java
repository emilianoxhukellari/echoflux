package echoflux.core.diarization;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiarizationEntry {

    @NotBlank
    private String speakerName;

    @Min(0)
    private long startOffsetMillis;

    @Min(0)
    private long endOffsetMillis;

}
