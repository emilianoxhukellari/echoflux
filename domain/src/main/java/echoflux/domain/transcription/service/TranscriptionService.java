package echoflux.domain.transcription.service;

import echoflux.core.document.Paragraph;
import echoflux.core.word.common.WordInfo;
import echoflux.domain.transcription.data.ScalarTranscriptionProjection;
import echoflux.domain.transcription_word.data.SequencedWord;
import echoflux.domain.transcription_word.data.SpeakerSegment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import echoflux.domain.transcription.data.TranscriptionEntity;

import java.util.List;

@Validated
public interface TranscriptionService {

    TranscriptionEntity getById(@NotNull Long id);

    TranscriptionEntity getWithWordsById(@NotNull Long id);

    ScalarTranscriptionProjection getScalarProjectedById(@NotNull Long id);

    ScalarTranscriptionProjection create(@Valid @NotNull CreateTranscriptionCommand command);

    ScalarTranscriptionProjection patch(@Valid @NotNull PatchTranscriptionCommand command);

    ScalarTranscriptionProjection rename(@Valid @NotNull RenameTranscriptionCommand command);

    List<SequencedWord> getTranscriptionWords(@NotNull Long transcriptionId);

    List<SpeakerSegment> getTranscriptionSpeakerSegments(@NotNull Long transcriptionId);

    List<Paragraph> renderTranscript(@NotNull Long transcriptionId, boolean withTimestamps);

    <T extends WordInfo> void saveWords(@NotNull Long transcriptionId, @NotNull List<T> words);

}
