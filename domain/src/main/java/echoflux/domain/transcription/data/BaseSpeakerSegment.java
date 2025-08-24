package echoflux.domain.transcription.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import echoflux.core.word.common.BaseSpeakerSegmentInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseSpeakerSegment implements BaseSpeakerSegmentInfo {

    @JsonProperty("speakerName")
    private String speakerName;

    @JsonProperty("content")
    private String content;

}
