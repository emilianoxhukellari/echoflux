package transcribe.core.transcribe.google;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.longrunning.OperationTimedPollAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v2.AutoDetectDecodingConfig;
import com.google.cloud.speech.v2.BatchRecognizeFileMetadata;
import com.google.cloud.speech.v2.BatchRecognizeRequest;
import com.google.cloud.speech.v2.InlineOutputConfig;
import com.google.cloud.speech.v2.RecognitionConfig;
import com.google.cloud.speech.v2.RecognitionFeatures;
import com.google.cloud.speech.v2.RecognitionOutputConfig;
import com.google.cloud.speech.v2.RecognizerName;
import com.google.cloud.speech.v2.SpeechClient;
import com.google.cloud.speech.v2.SpeechRecognitionAlternative;
import com.google.cloud.speech.v2.SpeechRecognitionResult;
import com.google.cloud.speech.v2.SpeechSettings;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;
import transcribe.core.core.executor.MoreExecutors;
import transcribe.core.core.log.LoggedMethodExecution;
import transcribe.core.core.provider.AiProvider;
import transcribe.core.core.utils.TsLists;
import transcribe.core.properties.GoogleCloudProperties;
import transcribe.core.settings.SettingsLoader;
import transcribe.core.transcribe.SpeechToText;
import transcribe.core.transcribe.common.Language;
import transcribe.core.word.common.SpeechToTextWord;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class GoogleSpeechToText implements SpeechToText {

    private final String implicitRecognizer;
    private final SettingsLoader settingsLoader;
    private final GoogleCloudProperties googleCloudProperties;

    public GoogleSpeechToText(GoogleCloudProperties googleCloudProperties,
                              SettingsLoader settingsLoader) {
        this.googleCloudProperties = googleCloudProperties;
        this.settingsLoader = settingsLoader;
        this.implicitRecognizer = newImplicitRecognizer(googleCloudProperties);
    }

    @SneakyThrows
    @LoggedMethodExecution(logReturn = false)
    public List<SpeechToTextWord> transcribe(URI cloudUri, List<Language> languages) {
        var settings = settingsLoader.load(GoogleSpeechSettings.class);

        var recognitionConfig = newRecognitionConfig(languages, settings);
        var uri = cloudUri.toString();

        var batchRecognizeFileMetadata = BatchRecognizeFileMetadata.newBuilder()
                .setUri(uri)
                .build();
        var recognitionOutputConfig = RecognitionOutputConfig.newBuilder()
                .setInlineResponseConfig(InlineOutputConfig.newBuilder().build())
                .build();

        var batchRecognizeRequest = BatchRecognizeRequest.newBuilder()
                .setRecognitionOutputConfig(recognitionOutputConfig)
                .setConfig(recognitionConfig)
                .addFiles(batchRecognizeFileMetadata)
                .setRecognizer(implicitRecognizer)
                .build();

        @Cleanup
        var speechClient = newSpeechClient(googleCloudProperties, settings);

        var response = speechClient.batchRecognizeOperationCallable().call(batchRecognizeRequest);
        var singleResponse = response.getResultsMap().get(uri);
        var resultsList = singleResponse.getInlineResult().getTranscript().getResultsList();

        var recognitionParts = ListUtils.emptyIfNull(resultsList)
                .stream()
                .map(SpeechRecognitionResult::getAlternativesList)
                .filter(CollectionUtils::isNotEmpty)
                .map(List::getFirst)
                .toList();

        return recognitionParts.stream()
                .map(SpeechRecognitionAlternative::getWordsList)
                .flatMap(List::stream)
                .map(w -> SpeechToTextWord.builder()
                        .startOffsetMillis(protobufDurationToMillis(w.getStartOffset()))
                        .endOffsetMillis(protobufDurationToMillis(w.getEndOffset()))
                        .content(w.getWord())
                        .build()
                )
                .toList();
    }

    @Override
    public AiProvider getProvider() {
        return AiProvider.GOOGLE;
    }

    private static RecognitionConfig newRecognitionConfig(List<Language> languages, GoogleSpeechSettings settings) {
        Validate.notEmpty(languages);
        Objects.requireNonNull(settings);

        var recognitionFeatures = RecognitionFeatures.newBuilder()
                .setEnableAutomaticPunctuation(settings.getEnableAutomaticPunctuation())
                .setEnableWordTimeOffsets(true)
                .build();

        return RecognitionConfig.newBuilder()
                .setAutoDecodingConfig(AutoDetectDecodingConfig.newBuilder().build())
                .addAllLanguageCodes(TsLists.collect(languages, Language::getBcp47))
                .setModel(settings.getModel())
                .setFeatures(recognitionFeatures)
                .build();
    }

    @SneakyThrows
    private static SpeechClient newSpeechClient(GoogleCloudProperties properties, GoogleSpeechSettings settings) {
        Objects.requireNonNull(properties);
        Objects.requireNonNull(settings);

        @Cleanup
        var privateKeyStream = IOUtils.toInputStream(properties.getPrivateKey(), StandardCharsets.UTF_8);
        var credentials = GoogleCredentials.fromStream(privateKeyStream).createScoped(SpeechSettings.getDefaultServiceScopes());

        var retrySettings = RetrySettings.newBuilder()
                .setInitialRetryDelayDuration(Duration.ofSeconds(settings.getInitialRetryDelayDurationSeconds()))
                .setRetryDelayMultiplier(settings.getRetryDelayMultiplier())
                .setMaxRetryDelayDuration(Duration.ofSeconds(settings.getMaxRetryDelayDurationSeconds()))
                .setTotalTimeoutDuration(Duration.ofMinutes(settings.getTotalTimeoutDurationMinutes()))
                .build();

        var grpcChannelProvider = InstantiatingGrpcChannelProvider.newBuilder()
                .setExecutor(MoreExecutors.virtualThreadExecutor())
                .build();

        var settingsBuilder = SpeechSettings.newBuilder()
                .setTransportChannelProvider(grpcChannelProvider)
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .setEndpoint(properties.getSpeechEndpoint());

        settingsBuilder.batchRecognizeOperationSettings()
                .setPollingAlgorithm(OperationTimedPollAlgorithm.create(retrySettings));

        return SpeechClient.create(settingsBuilder.build());
    }

    private static String newImplicitRecognizer(GoogleCloudProperties properties) {
        return RecognizerName.of(properties.getProjectId(), properties.getLocation(), "_").toString();
    }

    private static long protobufDurationToMillis(com.google.protobuf.Duration duration) {
        return duration.getSeconds() * 1_000L + duration.getNanos() / 1_000_000L;
    }

}
