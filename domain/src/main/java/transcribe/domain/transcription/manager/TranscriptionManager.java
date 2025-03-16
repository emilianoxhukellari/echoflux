package transcribe.domain.transcription.manager;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.core.word.common.Word;
import transcribe.domain.transcription_word.data.SpeakerSegmentDto;
import transcribe.domain.transcription_word.data.WordDto;

import java.util.List;

@Validated
public interface TranscriptionManager {

    List<WordDto> getTranscriptionSpeakerWords(@NotNull Long transcriptionId);

    List<SpeakerSegmentDto> getTranscriptionSpeakerSegments(@NotNull Long transcriptionId);

    void createWords(@NotNull Long transcriptionId, @NotNull List<@Valid @NotNull Word> words);

    void replaceAllWords(@NotNull Long transcriptionId, @NotNull String words);

    void replaceWords(@Valid @NotNull ReplaceWordsCommand command);

}
