package echoflux.core.completions.openai;

import com.google.common.collect.Lists;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.beta.threads.Thread;
import com.openai.models.beta.threads.ThreadDeleteParams;
import com.openai.models.beta.threads.messages.MessageCreateParams;
import com.openai.models.beta.threads.messages.MessageListParams;
import com.openai.models.beta.threads.messages.Text;
import com.openai.models.beta.threads.messages.TextContentBlock;
import com.openai.models.beta.threads.runs.Run;
import com.openai.models.beta.threads.runs.RunCreateParams;
import com.openai.models.beta.threads.runs.RunRetrieveParams;
import com.openai.models.beta.threads.runs.RunStatus;
import com.openai.services.blocking.beta.ThreadService;
import com.openai.services.blocking.beta.threads.MessageService;
import com.openai.services.blocking.beta.threads.RunService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import echoflux.core.completions.CompletionResult;
import echoflux.core.completions.Completions;
import echoflux.core.completions.Tokens;
import echoflux.core.core.executor.MoreExecutors;
import echoflux.core.core.provider.AiProvider;
import echoflux.core.core.utils.TsFunctions;
import echoflux.core.properties.OpenAiProperties;
import echoflux.core.settings.SettingsLoader;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OpenAiCompletions implements Completions {

    private final SettingsLoader settingsLoader;
    private final ThreadService threadService;
    private final RunService runService;
    private final MessageService messageService;

    public OpenAiCompletions(OpenAiProperties openAiProperties,
                             SettingsLoader settingsLoader) {
        var openAIClient = newOpenAIClient(openAiProperties);
        this.settingsLoader = settingsLoader;
        this.threadService = openAIClient.beta().threads();
        this.runService = threadService.runs();
        this.messageService = threadService.messages();
    }

    @Override
    public CompletionResult complete(String input) {
        var settings = settingsLoader.load(OpenAiCompletionsSettings.class);

        var thread = threadService.create();
        var currentResponse = sendMessage(input, thread, settings);
        var responseList = Lists.newArrayList(currentResponse);

        int p = 1;
        while (currentResponse.isIncomplete()) {
            log.warn("Max tokens reached, requesting part [{}]", ++p);

            currentResponse = sendMessage(settings.getContinuePhrase(), thread, settings);
            responseList.add(currentResponse);
        }

        var threadDeleteParams = ThreadDeleteParams.builder()
                .threadId(thread.id())
                .build();

        threadService.delete(threadDeleteParams);

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

    private ContentResponse sendMessage(String message, Thread thread, OpenAiCompletionsSettings settings) {
        var messageCreateParams = MessageCreateParams.builder()
                .threadId(thread.id())
                .role(MessageCreateParams.Role.USER)
                .content(message)
                .build();

        var userMessage = messageService.create(messageCreateParams);

        var runCreateParams = RunCreateParams.builder()
                .assistantId(settings.getAssistantId())
                .threadId(thread.id())
                .maxCompletionTokens(settings.getRunMaxOutputTokens())
                .model(settings.getModel())
                .temperature(settings.getTemperature())
                .topP(settings.getTopP())
                .build();

        var run = runService.create(runCreateParams);

        var runRetrieveParams = RunRetrieveParams.builder()
                .threadId(thread.id())
                .runId(run.id())
                .build();

        var finishedRun = TsFunctions.pollUntil(
                () -> runService.retrieve(runRetrieveParams),
                this::isRunFinished,
                Duration.ofSeconds(5),
                Duration.ofMinutes(10)
        );

        if (!RunStatus.COMPLETED.equals(finishedRun.status()) && !RunStatus.INCOMPLETE.equals(finishedRun.status())) {
            var errorMessage = "Run did not succeed. Status: [%s]. Error: [%s]".formatted(
                    finishedRun.status(),
                    finishedRun.lastError().orElse(null)
            );
            throw new IllegalStateException(errorMessage);
        }

        var messageListParams = MessageListParams.builder()
                .threadId(thread.id())
                .limit(1)
                .order(MessageListParams.Order.ASC)
                .after(userMessage.id())
                .build();

        var newMessages = messageService.list(messageListParams);

        var assistantMessageText = newMessages.data()
                .getFirst()
                .content()
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No content item found"))
                .text()
                .map(TextContentBlock::text)
                .map(Text::value)
                .orElse(StringUtils.EMPTY);

        return ContentResponse.builder()
                .text(assistantMessageText)
                .isIncomplete(RunStatus.INCOMPLETE.equals(finishedRun.status()))
                .inTokens(finishedRun.usage().map(Run.Usage::promptTokens).orElse(0L))
                .outTokens(finishedRun.usage().map(Run.Usage::completionTokens).orElse(0L))
                .build();
    }

    private boolean isRunFinished(Run run) {
        Objects.requireNonNull(run, "Run must not be null");

        return !RunStatus.QUEUED.equals(run.status()) && !RunStatus.IN_PROGRESS.equals(run.status());
    }

    private static OpenAIClient newOpenAIClient(OpenAiProperties openAiProperties) {
        return OpenAIOkHttpClient.builder()
                .apiKey(openAiProperties.getApiKey())
                .maxRetries(5)
                .timeout(Duration.ofMinutes(10))
                .streamHandlerExecutor(MoreExecutors.virtualThreadExecutor())
                .build();
    }

    @Builder
    private record ContentResponse(String text, boolean isIncomplete, long inTokens, long outTokens) {
    }

}
