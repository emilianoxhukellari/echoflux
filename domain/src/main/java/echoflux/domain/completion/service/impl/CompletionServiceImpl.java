package echoflux.domain.completion.service.impl;

import echoflux.domain.completion.service.CompletionStatus;
import echoflux.domain.jooq.tables.pojos.Completion;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import echoflux.domain.completion.mapper.CompletionMapper;
import echoflux.domain.completion.service.CompletionService;
import echoflux.domain.completion.service.CreateCompletionCommand;
import echoflux.domain.completion.service.PatchCompletionCommand;

import static echoflux.domain.jooq.Tables.COMPLETION;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompletionServiceImpl implements CompletionService {

    private final DSLContext ctx;
    private final CompletionMapper completionMapper;

    @Override
    @Transactional
    public Completion create(CreateCompletionCommand command) {
        var record = ctx.newRecord(COMPLETION);
        record.setInput(command.getInput());
        record.setTranscriptionId(command.getTranscriptionId());
        record.setStatus(CompletionStatus.SUCCESS);
        record.store();

        return record.into(Completion.class);
    }

    @Override
    @Transactional
    public Completion patch(PatchCompletionCommand command) {
        var record = ctx.fetchSingle(COMPLETION, COMPLETION.ID.eq(command.getId()));
        var patchedRecord = completionMapper.patch(record, command);
        patchedRecord.store();

        return patchedRecord.into(Completion.class);
    }

}
