package transcribe.core.transcribe;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.core.core.log.LoggedMethodExecution;
import transcribe.core.transcribe.common.Language;
import transcribe.core.transcribe.common.Word;

import java.net.URI;
import java.util.List;

@Validated
public interface SpeechToText {

    List<Word> transcribe(@NotNull URI cloudUri, @NotEmpty List<@NotNull Language> languages);

    @LoggedMethodExecution(logReturn = false)
    default List<Word> transcribe(@NotNull URI cloudUri, @NotNull Language language) {
        return transcribe(cloudUri, List.of(language));
    }

}
