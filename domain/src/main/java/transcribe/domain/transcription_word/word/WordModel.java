package transcribe.domain.transcription_word.word;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordModel {

    @NotBlank
    private String content;

    @Min(0)
    @NotNull
    private Long startOffsetMillis;

    @Min(0)
    @NotNull
    private Long endOffsetMillis;

    @Min(0)
    @NotNull
    private Integer sequence;

}
