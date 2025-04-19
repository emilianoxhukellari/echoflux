package echoflux.domain.transcription_word.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceCreator;
import echoflux.core.word.common.WordInfo;

@Data
@Builder
@AllArgsConstructor(onConstructor_ = @PersistenceCreator)
@NoArgsConstructor
public class WordDto implements WordInfo {

    @NotBlank
    @Getter(onMethod_ = @Override)
    @Setter(onMethod_ = @Override)
    @JsonProperty("content")
    private String content;

    @NotBlank
    @Getter(onMethod_ = @Override)
    @Setter(onMethod_ = @Override)
    @JsonProperty("speakerName")
    private String speakerName;

    @Min(0)
    @Getter(onMethod_ = @Override)
    @Setter(onMethod_ = @Override)
    @JsonProperty("startOffsetMillis")
    private Long startOffsetMillis;

    @Min(0)
    @Getter(onMethod_ = @Override)
    @Setter(onMethod_ = @Override)
    @JsonProperty("endOffsetMillis")
    private Long endOffsetMillis;

    @Min(0)
    @JsonProperty("sequence")
    private Integer sequence;

}
