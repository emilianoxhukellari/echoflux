package transcribe.domain.transcript.transcript_part.service;

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
public class CreateTranscriptPartCommand {

    @NotNull
    private Long transcriptionId;

    @NotNull
    @Min(0)
    private Long startOffsetMillis;

    @NotNull
    @Min(0)
    private Long endOffsetMillis;

    @NotNull
    @Min(0)
    private Integer sequence;

    @NotNull
    private Boolean endOfPartition;

}
