package echoflux.core.transcribe;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import echoflux.core.core.log.LoggedMethodExecution;
import echoflux.core.core.provider.AiProviderAware;
import echoflux.core.transcribe.common.Language;
import echoflux.core.word.common.SpeechToTextWord;

import java.net.URI;
import java.util.List;

@Validated
public interface SpeechToText extends AiProviderAware {

    @LoggedMethodExecution(logReturn = false)
    default List<SpeechToTextWord> transcribe(@NotNull URI cloudUri, @NotNull Language language) {
        return transcribe(cloudUri, List.of(language));
    }

    List<SpeechToTextWord> transcribe(@NotNull URI cloudUri, @NotEmpty List<@NotNull Language> languages);

}
