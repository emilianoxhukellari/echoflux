package transcribe.domain.transcription.service;

import jakarta.validation.constraints.Max;
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
public class UpdateTranscriptionMetadataCommand {

    @NotNull
    private Long id;

    @Min(0)
    @Max(100)
    private Integer transcribeProgress;

    @Min(0)
    private Long lengthMillis;

    @Min(0)
    private Long downloadDurationMillis;

    @Min(0)
    private Long processDurationMillis;

    @Min(0)
    private Long transcribeDurationMillis;

}
