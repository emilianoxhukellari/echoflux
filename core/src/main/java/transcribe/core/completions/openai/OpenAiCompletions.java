package transcribe.core.completions.openai;

import com.azure.ai.openai.assistants.AssistantsClient;
import com.azure.ai.openai.assistants.AssistantsClientBuilder;
import com.azure.ai.openai.assistants.models.AssistantThread;
import com.azure.ai.openai.assistants.models.AssistantThreadCreationOptions;
import com.azure.ai.openai.assistants.models.CreateRunOptions;
import com.azure.ai.openai.assistants.models.ListSortOrder;
import com.azure.ai.openai.assistants.models.MessageRole;
import com.azure.ai.openai.assistants.models.MessageTextContent;
import com.azure.ai.openai.assistants.models.RunStatus;
import com.azure.ai.openai.assistants.models.ThreadMessageOptions;
import com.azure.ai.openai.assistants.models.ThreadRun;
import com.azure.core.credential.KeyCredential;
import com.azure.core.http.policy.ExponentialBackoffOptions;
import com.azure.core.http.policy.RetryOptions;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import transcribe.core.completions.CompletionResult;
import transcribe.core.completions.Completions;
import transcribe.core.completions.Tokens;
import transcribe.core.core.provider.AiProvider;
import transcribe.core.core.utils.MoreFunctions;
import transcribe.core.properties.OpenAiProperties;
import transcribe.core.settings.SettingsLoader;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OpenAiCompletions implements Completions {

    private final static RunStatus INCOMPLETE_STATUS = RunStatus.fromString("incomplete");
    private final AssistantsClient assistantsClient;
    private final SettingsLoader settingsLoader;

    public OpenAiCompletions(OpenAiProperties openAiProperties,
                             SettingsLoader settingsLoader) {
        this.assistantsClient = newAssistantsClient(openAiProperties);
        this.settingsLoader = settingsLoader;
    }

    @Override
    public CompletionResult complete(String input) {
        var settings = settingsLoader.load(OpenAiCompletionsSettings.class);

        var thread = assistantsClient.createThread(new AssistantThreadCreationOptions());

        var currentResponse = sendMessage(input, thread, settings);
        var responseList = Lists.newArrayList(currentResponse);

        int p = 1;
        while (currentResponse.isIncomplete()) {
            log.info("Max tokens reached, requesting part [{}]", ++p);

            currentResponse = sendMessage(settings.getContinuePhrase(), thread, settings);
            responseList.add(currentResponse);
        }

        assistantsClient.deleteThread(thread.getId());

        var output = responseList.stream()
                .map(ContentResponse::text)
                .collect(Collectors.joining());

        var tokens = responseList.stream()
                .map(
                        r -> Tokens.builder()
                                .in(r.inTokens())
                                .out(r.outTokens())
                                .build()
                )
                .reduce(Tokens::add)
                .orElseGet(Tokens::empty);

        return CompletionResult.builder()
                .output(output)
                .inputTokens(tokens.in())
                .outputTokens(tokens.out())
                .model(settings.getModel())
                .temperature(settings.getTemperature())
                .topP(settings.getTopP())
                .build();
    }

    @Override
    public AiProvider getProvider() {
        return AiProvider.OPENAI;
    }

    //todo: why no usage info on max completions hit?
    private ContentResponse sendMessage(String message, AssistantThread thread, OpenAiCompletionsSettings settings) {
        var threadCreationOptions = new ThreadMessageOptions(MessageRole.USER, message);

        var userMessage = assistantsClient.createMessage(thread.getId(), threadCreationOptions);

        var runOptions = new CreateRunOptions(settings.getAssistantId())
                .setMaxCompletionTokens(settings.getRunMaxOutputTokens())
                .setModel(settings.getModel())
                .setTemperature(settings.getTemperature())
                .setTopP(settings.getTopP());

        var run = assistantsClient.createRun(thread.getId(), runOptions);

        var finishedRun = MoreFunctions.pollUntil(
                () -> assistantsClient.getRun(thread.getId(), run.getId()),
                this::isRunFinished,
                Duration.ofSeconds(3),
                Duration.ofMinutes(10)
        );

        if (finishedRun.getStatus() != RunStatus.COMPLETED && finishedRun.getStatus() != INCOMPLETE_STATUS) {
            var errorMessage = "Run did not succeed. Status: [%s]. Error: [%s]".formatted(
                    finishedRun.getStatus(),
                    finishedRun.getLastError().getMessage()
            );
            throw new IllegalStateException(errorMessage);
        }

        var newMessages = assistantsClient.listMessages(
                thread.getId(),
                1,
                ListSortOrder.ASCENDING,
                userMessage.getId(),
                null
        );

        var assistantMessageText = newMessages.getData()
                .getFirst()
                .getContent()
                .stream()
                .map(MessageTextContent.class::cast)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No content item found"))
                .getText()
                .getValue();

        long inTokens = 0;
        long outTokens = 0;

        if (finishedRun.getUsage() != null) {
            inTokens = finishedRun.getUsage().getPromptTokens();
            outTokens = finishedRun.getUsage().getCompletionTokens();
        }

        return ContentResponse.builder()
                .text(assistantMessageText)
                .isIncomplete(finishedRun.getStatus() == INCOMPLETE_STATUS)
                .inTokens(inTokens)
                .outTokens(outTokens)
                .build();
    }

    private boolean isRunFinished(ThreadRun threadRun) {
        Objects.requireNonNull(threadRun, "Thread run must not be null");

        return threadRun.getStatus() != RunStatus.QUEUED && threadRun.getStatus() != RunStatus.IN_PROGRESS;
    }

    private static AssistantsClient newAssistantsClient(OpenAiProperties openAiProperties) {
        var exponentialBackoffOptions = new ExponentialBackoffOptions()
                .setBaseDelay(Duration.ofSeconds(10))
                .setMaxDelay(Duration.ofSeconds(60))
                .setMaxRetries(10);

        return new AssistantsClientBuilder()
                .credential(new KeyCredential(openAiProperties.getApiKey()))
                .retryOptions(new RetryOptions(exponentialBackoffOptions))
                .buildClient();
    }

    @Builder
    private record ContentResponse(String text, boolean isIncomplete, long inTokens, long outTokens) {
    }

}
