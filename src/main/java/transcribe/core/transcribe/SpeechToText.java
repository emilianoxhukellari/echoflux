package transcribe.core.transcribe;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.common.log.LogMethodExecution;
import transcribe.core.transcribe.common.Language;
import transcribe.core.transcribe.common.TranscribeResult;

import java.net.URI;
import java.util.List;

@Validated
public interface SpeechToText {

    @LogMethodExecution(logReturn = false)
    TranscribeResult transcribe(@NotNull URI cloudUri, @NotEmpty List<@NotNull Language> languages);

    @LogMethodExecution(logReturn = false)
    default TranscribeResult transcribe(@NotNull URI cloudUri, @NotNull Language language) {
        return transcribe(cloudUri, List.of(language));
    }

}
