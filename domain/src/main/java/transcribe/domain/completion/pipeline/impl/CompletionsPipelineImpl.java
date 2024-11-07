package transcribe.domain.completion.pipeline.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import transcribe.core.completions.Completions;
import transcribe.core.function.FunctionUtils;
import transcribe.domain.completion.data.CompletionEntity;
import transcribe.domain.completion.data.CompletionStatus;
import transcribe.domain.completion.mapper.CompletionMapper;
import transcribe.domain.completion.pipeline.CompletionsPipeline;
import transcribe.domain.completion.pipeline.CompletionsPipelineResult;
import transcribe.domain.completion.service.CompletionService;
import transcribe.domain.completion.service.CreateCompletionCommand;
import transcribe.domain.completion.service.UpdateCompletionCommand;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompletionsPipelineImpl implements CompletionsPipeline {

    private final Completions completions;
    private final CompletionService service;
    private final CompletionMapper mapper;

    @Override
    public Optional<CompletionsPipelineResult> complete(String input) {
        if (StringUtils.isBlank(input)) {
            log.warn("Input is blank, skipping completion");

            return Optional.empty();
        }

        var entity = service.create(
                CreateCompletionCommand.builder()
                        .input(input)
                        .build()
        );

        try {
            return Optional.of(completeCreated(entity));
        } catch (Throwable e) {
            service.update(
                    UpdateCompletionCommand.builder()
                            .id(entity.getId())
                            .error(e.getMessage())
                            .status(CompletionStatus.FAILED)
                            .build()
            );

            return Optional.empty();
        }
    }

    private CompletionsPipelineResult completeCreated(CompletionEntity entity) {
        Objects.requireNonNull(entity, "Entity cannot be null");

        service.update(
                UpdateCompletionCommand.builder()
                        .id(entity.getId())
                        .status(CompletionStatus.PROCESSING)
                        .build()
        );

        var timedResult = FunctionUtils.getTimed(() -> completions.complete(entity.getInput()));
        var completionResult = timedResult.getResult();

        var completedEntity = service.update(
                mapper.toCommand(completionResult)
                        .withId(entity.getId())
                        .withStatus(CompletionStatus.COMPLETED)
                        .withDurationMillis(timedResult.getDuration().toMillis())
        );

        return mapper.toResult(completedEntity);
    }

}
