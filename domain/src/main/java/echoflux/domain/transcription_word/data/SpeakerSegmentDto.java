package echoflux.domain.transcription_word.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import echoflux.core.word.common.SpeakerSegmentInfo;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpeakerSegmentDto implements SpeakerSegmentInfo<WordDto> {

    @NotBlank
    @Getter(onMethod_ = @Override)
    @Setter(onMethod_ = @Override)
    @JsonProperty("speakerName")
    private String speakerName;

    @Min(0)
    @NotNull
    @Getter(onMethod_ = @Override)
    @Setter(onMethod_ = @Override)
    @JsonProperty("startOffsetMillis")
    private Long startOffsetMillis;

    @Min(0)
    @NotNull
    @Getter(onMethod_ = @Override)
    @Setter(onMethod_ = @Override)
    @JsonProperty("endOffsetMillis")
    private Long endOffsetMillis;

    @NotNull
    @Builder.Default
    @Getter(onMethod_ = @Override)
    @Setter(onMethod_ = @Override)
    @JsonProperty("words")
    private List<WordDto> words = List.of();

    @NotBlank
    @Getter(onMethod_ = @Override)
    @Setter(onMethod_ = @Override)
    @JsonProperty("content")
    private String content;

}
