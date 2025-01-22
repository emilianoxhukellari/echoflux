package transcribe.core.transcribe;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.core.core.log.LoggedMethodExecution;
import transcribe.core.core.provider.AiProviderAware;
import transcribe.core.transcribe.common.Language;
import transcribe.core.transcribe.common.SpeechToTextWord;

import java.net.URI;
import java.util.List;

@Validated
public interface SpeechToText extends AiProviderAware {

    @LoggedMethodExecution(logReturn = false)
    default List<SpeechToTextWord> transcribe(@NotNull URI cloudUri, @NotNull Language language) {
        return transcribe(cloudUri, List.of(language));
    }

    List<SpeechToTextWord> transcribe(@NotNull URI cloudUri, @NotEmpty List<@NotNull Language> languages);

    boolean supportsDiarization();

}
