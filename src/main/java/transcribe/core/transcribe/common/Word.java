package transcribe.core.transcribe.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Word {

    private String text;
    private long startOffset;
    private long endOffset;

}
