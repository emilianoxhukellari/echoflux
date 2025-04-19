package echoflux.core.word.common;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Word implements WordInfo {

    @NotBlank
    private String content;

    @NotBlank
    private String speakerName;

    @Min(0)
    private Long startOffsetMillis;

    @Min(0)
    private Long endOffsetMillis;

}
