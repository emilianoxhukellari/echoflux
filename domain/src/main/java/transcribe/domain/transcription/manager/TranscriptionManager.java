package transcribe.domain.transcription.manager;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.core.document.Paragraph;
import transcribe.core.word.common.WordInfo;
import transcribe.domain.transcription_word.data.SpeakerSegmentDto;
import transcribe.domain.transcription_word.data.WordDto;

import java.util.List;

@Validated
public interface TranscriptionManager {

    List<Paragraph> renderTranscript(@NotNull Long transcriptionId, boolean withTimestamps);

    List<WordDto> getTranscriptionSpeakerWords(@NotNull Long transcriptionId);

    List<SpeakerSegmentDto> getTranscriptionSpeakerSegments(@NotNull Long transcriptionId);

    <T extends WordInfo> void saveWords(@NotNull Long transcriptionId, @NotNull List<T> words);

}
