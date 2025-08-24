package echoflux.domain.transcription.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import echoflux.core.word.common.HasSequence;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import echoflux.core.word.common.WordInfo;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SequencedWord implements WordInfo, HasSequence {

    @NotBlank
    @JsonProperty("content")
    private String content;

    @NotBlank
    @JsonProperty("speakerName")
    private String speakerName;

    @Min(0)
    @JsonProperty("startOffsetMillis")
    private Long startOffsetMillis;

    @Min(0)
    @JsonProperty("endOffsetMillis")
    private Long endOffsetMillis;

    @Min(0)
    @JsonProperty("sequence")
    private Integer sequence;

}
