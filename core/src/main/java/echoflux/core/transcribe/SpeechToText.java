package echoflux.core.transcribe;

import echoflux.core.audio.common.AudioInfo;
import echoflux.core.word.common.Word;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import echoflux.core.core.provider.AiProviderAware;

import java.util.List;

@Validated
public interface SpeechToText extends AiProviderAware {

    List<Word> transcribe(@Valid @NotNull AudioInfo audioInfo, @NotNull Language language);

}
