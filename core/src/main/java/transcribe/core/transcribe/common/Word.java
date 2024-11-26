package transcribe.core.transcribe.common;

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
public class Word {

    @NotNull
    private String text;

    @Min(0)
    private long startOffsetMillis;

    @Min(0)
    private long endOffsetMillis;

}
