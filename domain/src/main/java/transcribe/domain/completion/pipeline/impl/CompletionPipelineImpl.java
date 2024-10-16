package transcribe.domain.completion.pipeline.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import transcribe.core.completions.Completions;
import transcribe.core.function.FunctionUtils;
import transcribe.domain.completion.data.CompletionStatus;
import transcribe.domain.completion.mapper.CompletionMapper;
import transcribe.domain.completion.pipeline.CompletionPipeline;
import transcribe.domain.completion.pipeline.CompletionPipelineResult;
import transcribe.domain.completion.service.CompletionService;
import transcribe.domain.completion.service.CreateCompletionCommand;
import transcribe.domain.completion.service.UpdateCompletionCommand;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompletionPipelineImpl implements CompletionPipeline {

    private final Completions completions;
    private final CompletionService service;
    private final CompletionMapper mapper;

    @Override
    public Optional<CompletionPipelineResult> complete(String input) {
        if (StringUtils.isBlank(input)) {
            log.warn("Input is blank, skipping completion");

            return Optional.empty();
        }

        service.create(
                CreateCompletionCommand.builder()
                        .input(input)
                        .build()
        );

        try {
            return Optional.of(runCreated(input));
        } catch (Throwable e) {
            service.update(
                    UpdateCompletionCommand.builder()
                            .error(e.getMessage())
                            .status(CompletionStatus.FAILED)
                            .build()
            );

            return Optional.empty();
        }
    }

    private CompletionPipelineResult runCreated(String input) {
        service.update(
                UpdateCompletionCommand.builder()
                        .status(CompletionStatus.PROCESSING)
                        .build()
        );

        var timedResult = FunctionUtils.getTimed(() -> completions.complete(input));

        var completion = timedResult.getResult();
        var updateCommand = mapper.toCommand(completion, CompletionStatus.COMPLETED, timedResult.getDuration().toMillis());
        var entity = service.update(updateCommand);

        return mapper.toResult(entity);
    }

}
