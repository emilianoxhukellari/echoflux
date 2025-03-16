package transcribe.domain.completion.pipeline.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import transcribe.core.completions.Completions;
import transcribe.core.core.bean.loader.BeanLoader;
import transcribe.core.core.log.LoggedMethodExecution;
import transcribe.core.core.utils.MoreFunctions;
import transcribe.core.settings.SettingsLoader;
import transcribe.domain.completion.data.CompletionProjection;
import transcribe.domain.completion.data.CompletionStatus;
import transcribe.domain.completion.mapper.CompletionMapper;
import transcribe.domain.completion.pipeline.CompleteCommand;
import transcribe.domain.completion.pipeline.CompletionsPipeline;
import transcribe.domain.completion.pipeline.CompletionsPipelineSettings;
import transcribe.domain.completion.service.CompletionService;
import transcribe.domain.completion.service.CreateCompletionCommand;
import transcribe.domain.completion.service.PatchCompletionCommand;

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
    public CompletionProjection complete(CompleteCommand command) {
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
            var completions = beanLoader.getWhen(
                    Completions.class,
                    c -> Objects.equals(c.getProvider(), provider)
            );

            return completeCreated(completion, completions);
        } catch (Throwable e) {
            completionService.patch(
                    PatchCompletionCommand.builder()
                            .id(completion.id())
                            .error(e.getMessage())
                            .status(CompletionStatus.FAILED)
                            .build()
            );

            throw e;
        }
    }

    private CompletionProjection completeCreated(CompletionProjection completion, Completions completions) {
        Objects.requireNonNull(completion, "Completion cannot be null");
        Objects.requireNonNull(completions, "Completions cannot be null");

        completionService.patch(
                PatchCompletionCommand.builder()
                        .id(completion.id())
                        .status(CompletionStatus.PROCESSING)
                        .build()
        );

        var timedResult = MoreFunctions.getTimed(() -> completions.complete(completion.input()));
        var completionResult = timedResult.getResult();

        var patchCommand = completionMapper.toCommand(
                completion.id(),
                CompletionStatus.COMPLETED,
                timedResult.getDuration().toMillis(),
                completionResult
        );

        return completionService.patch(patchCommand);
    }

}
