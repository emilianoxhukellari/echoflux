package echoflux.core.word.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleContent implements HasContent {

    private String content;

}
