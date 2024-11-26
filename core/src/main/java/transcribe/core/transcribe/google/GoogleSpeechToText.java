package transcribe.core.transcribe.google;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.longrunning.OperationTimedPollAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v2.*;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import transcribe.core.core.log.LoggedMethodExecution;
import transcribe.core.core.qualifier.Qualifiers;
import transcribe.core.core.utils.MoreLists;
import transcribe.core.properties.GoogleCloudProperties;
import transcribe.core.settings.SettingsLoader;
import transcribe.core.transcribe.GoogleSpeechSettings;
import transcribe.core.transcribe.SpeechToText;
import transcribe.core.transcribe.common.Language;
import transcribe.core.transcribe.common.Word;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j
public class GoogleSpeechToText implements SpeechToText {

    private final String implicitRecognizer;
    private final SettingsLoader settingsLoader;
    private final GoogleCloudProperties googleCloudProperties;
    private final ExecutorService executorService;

    public GoogleSpeechToText(GoogleCloudProperties googleCloudProperties,
                              @Qualifier(Qualifiers.VIRTUAL_THREAD_EXECUTOR) ExecutorService executorService,
                              SettingsLoader settingsLoader) {
        this.googleCloudProperties = googleCloudProperties;
        this.executorService = executorService;
        this.settingsLoader = settingsLoader;
        this.implicitRecognizer = newImplicitRecognizer(googleCloudProperties);
    }

    @SneakyThrows
    @LoggedMethodExecution(logReturn = false)
    public List<Word> transcribe(URI cloudUri, List<Language> languages) {
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
        var speechClient = newSpeechClient(googleCloudProperties, executorService, settings);

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
                .map(w -> Word.builder()
                                .startOffsetMillis(protobufDurationToMillis(w.getStartOffset()))
                                .endOffsetMillis(protobufDurationToMillis(w.getEndOffset()))
                                .text(w.getWord())
                                .build())
                .toList();
    }

    private static RecognitionConfig newRecognitionConfig(List<Language> languages, GoogleSpeechSettings settings) {
        Validate.notEmpty(languages);
        Objects.requireNonNull(settings);

        var recognitionFeatures = RecognitionFeatures.newBuilder()
                .setEnableAutomaticPunctuation(settings.isEnableAutomaticPunctuation())
                .setEnableWordTimeOffsets(true)
                .build();

        return RecognitionConfig.newBuilder()
                .setAutoDecodingConfig(AutoDetectDecodingConfig.newBuilder().build())
                .addAllLanguageCodes(MoreLists.collect(languages, Language::getBcp47))
                .setModel(settings.getModel())
                .setFeatures(recognitionFeatures)
                .build();
    }

    @SneakyThrows
    private static SpeechClient newSpeechClient(GoogleCloudProperties properties,
                                                ExecutorService executorService,
                                                GoogleSpeechSettings settings) {
        Objects.requireNonNull(properties);
        Objects.requireNonNull(executorService);
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
                        .setExecutor(executorService)
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
