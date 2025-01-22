package transcribe.domain.transcript.transcript_part.part;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartModel {

    @NotNull
    private String text;

    @Min(0)
    @NotNull
    private Long startOffsetMillis;

    @Min(0)
    @NotNull
    private Long endOffsetMillis;

    @Min(0)
    @NotNull
    private Integer sequence;

    @NotNull
    private Boolean endOfPartition;

}
