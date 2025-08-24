package echoflux.domain.completion.pipeline.impl;

import echoflux.domain.jooq.tables.pojos.Completion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import echoflux.core.completions.Completions;
import echoflux.core.core.bean.accessor.BeanAccessor;
import echoflux.core.core.log.LoggedMethodExecution;
import echoflux.core.core.utils.MoreFunctions;
import echoflux.core.settings.SettingsLoader;
import echoflux.domain.completion.service.CompletionStatus;
import echoflux.domain.completion.pipeline.CompleteCommand;
import echoflux.domain.completion.pipeline.CompletionsPipeline;
import echoflux.domain.completion.pipeline.CompletionsPipelineSettings;
import echoflux.domain.completion.service.CompletionService;
import echoflux.domain.completion.service.CreateCompletionCommand;
import echoflux.domain.completion.service.PatchCompletionCommand;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CompletionsPipelineImpl implements CompletionsPipeline {

    private final CompletionService completionService;
    private final SettingsLoader settingsLoader;
    private final BeanAccessor beanAccessor;

    @LoggedMethodExecution(logArgs = false, logReturn = false)
    @Override
    public Completion complete(CompleteCommand command) {
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
            var completions = beanAccessor.getWhen(
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

    private Completion completeCreated(Completion completion, Completions completions) {
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

        var patchCommand = PatchCompletionCommand.builder()
                .id(completion.getId())
                .status(CompletionStatus.SUCCESS)
                .duration(timedResult.getDuration())
                .output(completionResult.getOutput())
                .inputTokens(completionResult.getInputTokens())
                .outputTokens(completionResult.getOutputTokens())
                .model(completionResult.getModel())
                .temperature(completionResult.getTemperature())
                .topP(completionResult.getTopP())
                .build();

        return completionService.patch(patchCommand);
    }

}
