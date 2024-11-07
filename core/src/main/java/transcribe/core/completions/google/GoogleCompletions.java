package transcribe.core.completions.google;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.Transport;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Candidate;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.LlmUtilityServiceClient;
import com.google.cloud.vertexai.api.LlmUtilityServiceSettings;
import com.google.cloud.vertexai.api.PredictionServiceClient;
import com.google.cloud.vertexai.api.PredictionServiceSettings;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.common.collect.Lists;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import transcribe.core.completions.CompletionResult;
import transcribe.core.completions.Completions;
import transcribe.core.core.qualifier.Qualifiers;
import transcribe.core.properties.GoogleCloudProperties;
import transcribe.core.settings.SettingsLoader;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j
public class GoogleCompletions implements Completions, DisposableBean {

    private final VertexAI vertexAI;
    private final GoogleCompletionsMapper mapper;
    private final SettingsLoader settingsLoader;

    public GoogleCompletions(GoogleCloudProperties properties,
                             @Qualifier(Qualifiers.VIRTUAL_THREAD_EXECUTOR) ExecutorService executorService,
                             GoogleCompletionsMapper mapper,
                             SettingsLoader settingsLoader) {
        this.vertexAI = newVertexAI(properties, executorService);
        this.mapper = mapper;
        this.settingsLoader = settingsLoader;
    }

    @Override
    @SneakyThrows
    public CompletionResult complete(String input) {
        var settings = settingsLoader.load(GoogleCompletionsSettings.class);

        var generationConfig = GenerationConfig.newBuilder()
                .setTemperature(settings.getTemperature())
                .setTopP(settings.getTopP())
                .build();

        var chatSession = new GenerativeModel(settings.getModel(), vertexAI)
                .withGenerationConfig(generationConfig)
                .startChat();

        var currentResponse = chatSession.sendMessage(input);
        var responseList = Lists.newArrayList(currentResponse);

        while (Candidate.FinishReason.MAX_TOKENS == ResponseHandler.getFinishReason(currentResponse)) {
            log.info("Max tokens reached, requesting next part");

            currentResponse = chatSession.sendMessage(settings.getContinuePhrase());
            responseList.add(currentResponse);
        }

        var output = mapper.toOutput(responseList);
        var tokens = mapper.toTokens(responseList);

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

    private static VertexAI newVertexAI(GoogleCloudProperties properties, ExecutorService executorService) {
        var credentials = newCredentials(properties);
        var grpcChannelProvider = InstantiatingGrpcChannelProvider.newBuilder()
                .setExecutor(executorService)
                .build();

        return new VertexAI.Builder()
                .setProjectId(properties.getProjectId())
                .setLocation(properties.getLocation())
                .setApiEndpoint(properties.getAiPlatformEndpoint())
                .setTransport(Transport.GRPC)
                .setCredentials(credentials)
                .setPredictionClientSupplier(() -> newPredictionClient(
                        credentials,
                        grpcChannelProvider,
                        properties.getAiPlatformEndpoint()
                ))
                .setLlmClientSupplier(() -> newUtilityClient(
                        credentials,
                        grpcChannelProvider,
                        properties.getAiPlatformEndpoint()
                ))
                .build();
    }

    @SneakyThrows
    private static LlmUtilityServiceClient newUtilityClient(GoogleCredentials credentials,
                                                            InstantiatingGrpcChannelProvider grpcChannelProvider,
                                                            String endpoint) {
        var settings = LlmUtilityServiceSettings.newBuilder()
                .setTransportChannelProvider(grpcChannelProvider)
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .setEndpoint(endpoint)
                .build();

        return LlmUtilityServiceClient.create(settings);
    }

    @SneakyThrows
    private static PredictionServiceClient newPredictionClient(GoogleCredentials credentials,
                                                               InstantiatingGrpcChannelProvider grpcChannelProvider,
                                                               String endpoint) {
        var settings = PredictionServiceSettings.newBuilder()
                .setTransportChannelProvider(grpcChannelProvider)
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .setEndpoint(endpoint)
                .build();

        return PredictionServiceClient.create(settings);
    }

    @SneakyThrows
    private static GoogleCredentials newCredentials(GoogleCloudProperties properties) {
        @Cleanup
        var privateKeyStream = IOUtils.toInputStream(properties.getPrivateKey(), StandardCharsets.UTF_8);

        return GoogleCredentials.fromStream(privateKeyStream)
                .createScoped(PredictionServiceSettings.getDefaultServiceScopes());
    }

}
