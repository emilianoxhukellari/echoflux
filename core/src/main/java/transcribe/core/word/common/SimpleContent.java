package transcribe.core.word.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class SimpleContent implements HasContent {

    private String content;

}
