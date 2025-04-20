package echoflux.core.completions.google;

import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.cloud.ServiceOptions;
import com.google.cloud.vertexai.Transport;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Candidate;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.LlmUtilityServiceClient;
import com.google.cloud.vertexai.api.LlmUtilityServiceSettings;
import com.google.cloud.vertexai.api.PredictionServiceClient;
import com.google.cloud.vertexai.api.PredictionServiceSettings;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import echoflux.core.completions.CompletionResult;
import echoflux.core.completions.Completions;
import echoflux.core.completions.Tokens;
import echoflux.core.core.executor.MoreExecutors;
import echoflux.core.core.provider.AiProvider;
import echoflux.core.properties.GoogleCloudProperties;
import echoflux.core.settings.SettingsLoader;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GoogleCompletions implements Completions, DisposableBean {

    private final VertexAI vertexAI;
    private final SettingsLoader settingsLoader;

    public GoogleCompletions(GoogleCloudProperties properties,
                             SettingsLoader settingsLoader) {
        this.vertexAI = newVertexAI(properties);
        this.settingsLoader = settingsLoader;
    }

    @Override
    @SneakyThrows
    public CompletionResult complete(String input) {
        var settings = settingsLoader.load(GoogleCompletionsSettings.class);

        var generationConfig = GenerationConfig.newBuilder()
                .setTemperature(settings.getTemperature().floatValue())
                .setTopP(settings.getTopP().floatValue())
                .build();

        var chatSession = new GenerativeModel(settings.getModel(), vertexAI)
                .withGenerationConfig(generationConfig)
                .startChat();

        var currentResponse = chatSession.sendMessage(input);
        var responseList = Lists.newArrayList(currentResponse);

        int p = 1;
        while (Candidate.FinishReason.MAX_TOKENS == ResponseHandler.getFinishReason(currentResponse)) {
            log.warn("Max tokens reached, requesting part [{}]", ++p);

            currentResponse = chatSession.sendMessage(settings.getContinuePhrase());
            responseList.add(currentResponse);
        }

        var output = responseList.stream()
                .map(ResponseHandler::getText)
                .collect(Collectors.joining());

        var tokens = responseList
                .stream()
                .map(GoogleCompletions::toTokens)
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
    public void destroy() {
        vertexAI.close();
    }

    @Override
    public AiProvider getProvider() {
        return AiProvider.GOOGLE;
    }

    private static VertexAI newVertexAI(GoogleCloudProperties properties) {
        var grpcChannelProvider = InstantiatingGrpcChannelProvider.newBuilder()
                .setExecutor(MoreExecutors.virtualThreadExecutor())
                .build();

        return new VertexAI.Builder()
                .setProjectId(ServiceOptions.getDefaultProjectId())
                .setLocation(properties.getLocation())
                .setApiEndpoint(properties.getAiPlatformEndpoint())
                .setTransport(Transport.GRPC)
                .setPredictionClientSupplier(() -> newPredictionClient(
                        grpcChannelProvider,
                        properties.getAiPlatformEndpoint()
                ))
                .setLlmClientSupplier(() -> newUtilityClient(
                        grpcChannelProvider,
                        properties.getAiPlatformEndpoint()
                ))
                .build();
    }

    @SneakyThrows
    private static LlmUtilityServiceClient newUtilityClient(InstantiatingGrpcChannelProvider grpcChannelProvider,
                                                            String endpoint) {
        var settings = LlmUtilityServiceSettings.newBuilder()
                .setTransportChannelProvider(grpcChannelProvider)
                .setEndpoint(endpoint)
                .build();

        return LlmUtilityServiceClient.create(settings);
    }

    @SneakyThrows
    private static PredictionServiceClient newPredictionClient(InstantiatingGrpcChannelProvider grpcChannelProvider,
                                                               String endpoint) {
        var settings = PredictionServiceSettings.newBuilder()
                .setTransportChannelProvider(grpcChannelProvider)
                .setEndpoint(endpoint)
                .build();

        return PredictionServiceClient.create(settings);
    }

    private static Tokens toTokens(GenerateContentResponse response) {
        return Optional.ofNullable(response)
                .map(GenerateContentResponse::getUsageMetadata)
                .map(usage -> new Tokens(usage.getPromptTokenCount(), usage.getCandidatesTokenCount()))
                .orElse(Tokens.empty());
    }

}
