package transcribe.domain.completion.pipeline.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import transcribe.core.completions.Completions;
import transcribe.core.core.provider.AiProvider;
import transcribe.core.core.utils.MoreFunctions;
import transcribe.core.settings.SettingsLoader;
import transcribe.domain.completion.data.CompletionEntity;
import transcribe.domain.completion.data.CompletionStatus;
import transcribe.domain.completion.mapper.CompletionMapper;
import transcribe.domain.completion.pipeline.CompleteCommand;
import transcribe.domain.completion.pipeline.CompletionsPipeline;
import transcribe.domain.completion.pipeline.CompletionsPipelineSettings;
import transcribe.domain.completion.service.CompletionService;
import transcribe.domain.completion.service.CreateCompletionCommand;
import transcribe.domain.completion.service.PatchCompletionCommand;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompletionsPipelineImpl implements CompletionsPipeline {

    private final List<Completions> completionsList;
    private final CompletionService service;
    private final CompletionMapper mapper;
    private final SettingsLoader settingsLoader;

    @Override
    public CompletionEntity complete(CompleteCommand command) {
        var entity = service.create(
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
            var completions = getImplementation(provider);

            return completeCreated(entity, completions);
        } catch (Throwable e) {
            service.patch(
                    PatchCompletionCommand.builder()
                            .id(entity.getId())
                            .error(e.getMessage())
                            .status(CompletionStatus.FAILED)
                            .build()
            );

            throw e;
        }
    }

    private CompletionEntity completeCreated(CompletionEntity entity, Completions completions) {
        Objects.requireNonNull(entity, "Entity cannot be null");
        Objects.requireNonNull(completions, "Completions cannot be null");

        service.patch(
                PatchCompletionCommand.builder()
                        .id(entity.getId())
                        .status(CompletionStatus.PROCESSING)
                        .build()
        );

        var timedResult = MoreFunctions.getTimed(() -> completions.complete(entity.getInput()));
        var completionResult = timedResult.getResult();

        return service.patch(
                mapper.toCommand(completionResult)
                        .setId(entity.getId())
                        .setStatus(CompletionStatus.COMPLETED)
                        .setDurationMillis(timedResult.getDuration().toMillis())
        );
    }

    private Completions getImplementation(AiProvider aiProvider) {
        Objects.requireNonNull(aiProvider, "AI provider cannot be null");

        return completionsList.stream()
                .filter(c -> Objects.equals(c.getProvider(), aiProvider))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No completion implementation found for provider: " + aiProvider));
    }

}
