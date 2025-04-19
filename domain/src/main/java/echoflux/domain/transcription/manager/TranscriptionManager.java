package echoflux.domain.transcription.manager;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import echoflux.core.document.Paragraph;
import echoflux.core.word.common.WordInfo;
import echoflux.domain.transcription_word.data.SpeakerSegmentDto;
import echoflux.domain.transcription_word.data.WordDto;

import java.util.List;

@Validated
public interface TranscriptionManager {

    List<Paragraph> renderTranscript(@NotNull Long transcriptionId, boolean withTimestamps);

    List<WordDto> getTranscriptionSpeakerWords(@NotNull Long transcriptionId);

    List<SpeakerSegmentDto> getTranscriptionSpeakerSegments(@NotNull Long transcriptionId);

    <T extends WordInfo> void saveWords(@NotNull Long transcriptionId, @NotNull List<T> words);

}
