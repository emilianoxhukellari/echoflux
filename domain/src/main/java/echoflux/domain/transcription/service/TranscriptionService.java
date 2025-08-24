package echoflux.domain.transcription.service;

import echoflux.core.document.Paragraph;
import echoflux.core.word.common.WordInfo;
import echoflux.domain.jooq.tables.pojos.Transcription;
import echoflux.domain.transcription.data.SequencedWord;
import echoflux.domain.transcription.data.SpeakerSegment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface TranscriptionService {

    Transcription getById(@NotNull Long id);

    Transcription create(@Valid @NotNull CreateTranscriptionCommand command);

    Transcription patch(@Valid @NotNull PatchTranscriptionCommand command);

    Long rename(@Valid @NotNull RenameTranscriptionCommand command);

    List<SequencedWord> getTranscriptionWords(@NotNull Long transcriptionId);

    List<SpeakerSegment> getTranscriptionSpeakerSegments(@NotNull Long transcriptionId);

    List<Paragraph> renderTranscript(@NotNull Long transcriptionId, boolean withTimestamps);

    <T extends WordInfo> void saveWords(@NotNull Long transcriptionId, @NotNull List<T> words);

}
