package echoflux.domain.transcription.service.impl;

import echoflux.core.core.log.LoggedMethodExecution;
import echoflux.core.document.Paragraph;
import echoflux.core.word.common.WordInfo;
import echoflux.core.word.processor.SpeakerSegmentAssembler;
import echoflux.core.word.processor.TranscriptRenderer;
import echoflux.domain.jooq.tables.pojos.Transcription;
import echoflux.domain.transcription.data.SequencedWord;
import echoflux.domain.transcription.data.SpeakerSegment;
import echoflux.domain.transcription.data.TranscriptionStatus;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Records;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import echoflux.domain.transcription.mapper.TranscriptionMapper;
import echoflux.domain.transcription.service.CreateTranscriptionCommand;
import echoflux.domain.transcription.service.PatchTranscriptionCommand;
import echoflux.domain.transcription.service.RenameTranscriptionCommand;
import echoflux.domain.transcription.service.TranscriptionService;

import java.util.List;

import static echoflux.domain.jooq.Tables.TRANSCRIPTION;
import static echoflux.domain.jooq.Tables.TRANSCRIPTION_WORD;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TranscriptionServiceImpl implements TranscriptionService {

    private final DSLContext ctx;
    private final TranscriptionMapper transcriptionMapper;

    @Override
    public Transcription getById(Long id) {
        return ctx.fetchSingle(TRANSCRIPTION, TRANSCRIPTION.ID.eq(id))
                .into(Transcription.class);
    }

    @Override
    @Transactional
    public Transcription create(CreateTranscriptionCommand command) {
        var record = ctx.newRecord(TRANSCRIPTION);
        record.setName(command.getName());
        record.setLanguage(command.getLanguage());
        record.setApplicationUserId(command.getApplicationUserId());
        record.setSourceUri(command.getSourceUri());
        record.setStatus(TranscriptionStatus.CREATED);
        record.store();

        return record.into(Transcription.class);
    }

    @Override
    @Transactional
    public Transcription patch(PatchTranscriptionCommand command) {
        var record = ctx.fetchSingle(TRANSCRIPTION, TRANSCRIPTION.ID.eq(command.getId()));
        var patchedRecord = transcriptionMapper.patch(record, command);
        patchedRecord.store();

        return patchedRecord.into(Transcription.class);
    }

    @Transactional
    @Override
    public Long rename(RenameTranscriptionCommand command) {
        var record = ctx.fetchSingle(TRANSCRIPTION, TRANSCRIPTION.ID.eq(command.getId()));
        record.setName(command.getName());
        record.store();

        return record.getId();
    }

    @Override
    public List<SequencedWord> getTranscriptionWords(Long transcriptionId) {
        return ctx.select(
                        TRANSCRIPTION_WORD.CONTENT,
                        TRANSCRIPTION_WORD.SPEAKER_NAME,
                        TRANSCRIPTION_WORD.START_OFFSET_MILLIS,
                        TRANSCRIPTION_WORD.END_OFFSET_MILLIS,
                        TRANSCRIPTION_WORD.SEQUENCE
                )
                .from(TRANSCRIPTION_WORD)
                .where(TRANSCRIPTION_WORD.TRANSCRIPTION_ID.eq(transcriptionId))
                .orderBy(TRANSCRIPTION_WORD.SEQUENCE)
                .fetch(Records.mapping(SequencedWord::new));
    }

    @Override
    public List<SpeakerSegment> getTranscriptionSpeakerSegments(Long transcriptionId) {
        var words = getTranscriptionWords(transcriptionId);

        return SpeakerSegmentAssembler.assembleAll(words, SpeakerSegment::new);
    }

    @Override
    public List<Paragraph> renderTranscript(Long transcriptionId, boolean withTimestamps) {
        var speakerSegments = getTranscriptionSpeakerSegments(transcriptionId);

        return TranscriptRenderer.render(speakerSegments, withTimestamps);
    }

    @LoggedMethodExecution(logArgs = false, logReturn = false)
    @Transactional
    @Override
    public <T extends WordInfo> void saveWords(Long transcriptionId, List<T> words) {
        ctx.deleteFrom(TRANSCRIPTION_WORD)
                .where(TRANSCRIPTION_WORD.TRANSCRIPTION_ID.eq(transcriptionId))
                .and(TRANSCRIPTION_WORD.SEQUENCE.gt(words.size() - 1))
                .execute();

        var upsert = ctx.insertInto(
                TRANSCRIPTION_WORD,
                TRANSCRIPTION_WORD.TRANSCRIPTION_ID,
                TRANSCRIPTION_WORD.SEQUENCE,
                TRANSCRIPTION_WORD.CONTENT,
                TRANSCRIPTION_WORD.SPEAKER_NAME,
                TRANSCRIPTION_WORD.START_OFFSET_MILLIS,
                TRANSCRIPTION_WORD.END_OFFSET_MILLIS
        );

        for (int i = 0; i < words.size(); i++) {
            upsert = upsert.values(
                    transcriptionId,
                    i,
                    words.get(i).getContent(),
                    words.get(i).getSpeakerName(),
                    words.get(i).getStartOffsetMillis(),
                    words.get(i).getEndOffsetMillis()
            );
        }

        upsert.onConflict(TRANSCRIPTION_WORD.TRANSCRIPTION_ID, TRANSCRIPTION_WORD.SEQUENCE)
                .doUpdate()
                .set(TRANSCRIPTION_WORD.CONTENT, DSL.excluded(TRANSCRIPTION_WORD.CONTENT))
                .set(TRANSCRIPTION_WORD.SPEAKER_NAME, DSL.excluded(TRANSCRIPTION_WORD.SPEAKER_NAME))
                .set(TRANSCRIPTION_WORD.START_OFFSET_MILLIS, DSL.excluded(TRANSCRIPTION_WORD.START_OFFSET_MILLIS))
                .set(TRANSCRIPTION_WORD.END_OFFSET_MILLIS, DSL.excluded(TRANSCRIPTION_WORD.END_OFFSET_MILLIS))
                .execute();
    }

}
