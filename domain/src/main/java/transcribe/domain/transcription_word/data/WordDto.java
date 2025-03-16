package transcribe.domain.transcription_word.data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.PersistenceCreator;
import transcribe.core.word.common.WordInfo;

@Data
@Builder
@AllArgsConstructor(onConstructor_ = @PersistenceCreator)
@NoArgsConstructor
public class WordDto implements WordInfo {

    @NotBlank
    private String content;

    @NotBlank
    private String speakerName;

    @Min(0)
    private Long startOffsetMillis;

    @Min(0)
    private Long endOffsetMillis;

    @Min(0)
    private Integer sequence;

}
