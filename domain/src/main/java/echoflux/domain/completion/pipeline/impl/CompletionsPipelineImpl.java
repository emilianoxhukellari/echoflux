package echoflux.domain.completion.pipeline.impl;

import echoflux.domain.completion.data.ScalarCompletionProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import echoflux.core.completions.Completions;
import echoflux.core.core.bean.loader.BeanLoader;
import echoflux.core.core.log.LoggedMethodExecution;
import echoflux.core.core.utils.MoreFunctions;
import echoflux.core.settings.SettingsLoader;
import echoflux.domain.completion.data.CompletionStatus;
import echoflux.domain.completion.mapper.CompletionMapper;
import echoflux.domain.completion.pipeline.CompleteCommand;
import echoflux.domain.completion.pipeline.CompletionsPipeline;
import echoflux.domain.completion.pipeline.CompletionsPipelineSettings;
import echoflux.domain.completion.service.CompletionService;
import echoflux.domain.completion.service.CreateCompletionCommand;
import echoflux.domain.completion.service.PatchCompletionCommand;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompletionsPipelineImpl implements CompletionsPipeline {

    private final CompletionService completionService;
    private final CompletionMapper completionMapper;
    private final SettingsLoader settingsLoader;
    private final BeanLoader beanLoader;

    @LoggedMethodExecution(logArgs = false, logReturn = false)
    @Override
    public ScalarCompletionProjection complete(CompleteCommand command) {
        var completion = completionService.create(
                CreateCompletionCommand.builder()
                        .input(command.getInput())
                        .transcriptionId(command.getTranscriptionId())
                        .build()
        );

        try {
            var provider = Objects.requireNonNullElseGet(
                    command.getAiProvider(),
                    () -> settingsLoader.load(CompletionsPipelineSettings.class).getPreferredAiProvider()
            );
            var completions = beanLoader.loadWhen(
                    Completions.class,
                    c -> Objects.equals(c.getProvider(), provider)
            );

            return completeCreated(completion, completions);
        } catch (Throwable e) {
            completionService.patch(
                    PatchCompletionCommand.builder()
                            .id(completion.getId())
                            .error(e.getMessage())
                            .status(CompletionStatus.FAILED)
                            .build()
            );

            throw e;
        }
    }

    private ScalarCompletionProjection completeCreated(ScalarCompletionProjection completion, Completions completions) {
        Objects.requireNonNull(completion, "Completion cannot be null");
        Objects.requireNonNull(completions, "Completions cannot be null");

        completionService.patch(
                PatchCompletionCommand.builder()
                        .id(completion.getId())
                        .status(CompletionStatus.PROCESSING)
                        .build()
        );

        var timedResult = MoreFunctions.getTimed(() -> completions.complete(completion.getInput()));
        var completionResult = timedResult.getResult();

        var patchCommand = completionMapper.toCommand(completionResult);
        patchCommand.setId(completion.getId());
        patchCommand.setStatus(completion.getStatus());
        patchCommand.setDuration(completion.getDuration());

        return completionService.patch(patchCommand);
    }

}
