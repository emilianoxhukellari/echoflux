package echoflux.core.document;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import echoflux.core.core.utils.MoreStrings;
import echoflux.core.word.common.HasContent;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Paragraph implements HasContent {

    @NotNull
    private String content;

    public static Paragraph empty() {
        return new Paragraph(MoreStrings.EMPTY);
    }

}
