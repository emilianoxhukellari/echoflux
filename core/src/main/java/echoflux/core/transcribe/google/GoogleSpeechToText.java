package echoflux.core.transcribe.google;

import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.longrunning.OperationTimedPollAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.ServiceOptions;
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
import com.google.cloud.speech.v2.WordInfo;
import echoflux.core.audio.common.AudioInfo;
import echoflux.core.diarization.DiarizationEntry;
import echoflux.core.storage.SaveOptions;
import echoflux.core.storage.google.GoogleStorage;
import echoflux.core.core.utils.MoreFunctions;
import echoflux.core.diarization.AudioDiarizer;
import echoflux.core.word.common.Word;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;
import echoflux.core.core.executor.MoreExecutors;
import echoflux.core.core.log.LoggedMethodExecution;
import echoflux.core.core.provider.AiProvider;
import echoflux.core.properties.GoogleCloudProperties;
import echoflux.core.settings.SettingsLoader;
import echoflux.core.transcribe.SpeechToText;
import echoflux.core.transcribe.Language;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class GoogleSpeechToText implements SpeechToText {

    private final String implicitRecognizer;
    private final SettingsLoader settingsLoader;
    private final GoogleCloudProperties googleCloudProperties;
    private final GoogleStorage googleStorage;
    private final AudioDiarizer audioDiarizer;

    public GoogleSpeechToText(GoogleCloudProperties googleCloudProperties,
                              SettingsLoader settingsLoader,
                              GoogleStorage googleStorage,
                              AudioDiarizer audioDiarizer) {
        this.googleCloudProperties = googleCloudProperties;
        this.settingsLoader = settingsLoader;
        this.implicitRecognizer = newImplicitRecognizer(googleCloudProperties);
        this.googleStorage = googleStorage;
        this.audioDiarizer = audioDiarizer;
    }

    @LoggedMethodExecution(logReturn = false)
    @Override
    public List<Word> transcribe(AudioInfo audioInfo, Language language) {
        var saveOptions = SaveOptions.builder()
                .contentType(audioInfo.container().getContentType())
                .temp(true)
                .build();
        var uri = googleStorage.save(audioInfo.audio(), saveOptions);

        var result = MoreFunctions.executeAllParallelAbortOnFailure(
                () -> transcribe(uri, language),
                () -> diarize(uri)
        );
        MoreFunctions.runAsync(() -> googleStorage.delete(uri));

        return GoogleWordAssembler.assembleAll(result.t1(), result.t2());
    }

    @Override
    public AiProvider getProvider() {
        return AiProvider.GOOGLE;
    }

    private List<DiarizationEntry> diarize(URI uri) {
        var publicUrl = googleStorage.getSignedUrl(uri, Duration.ofHours(1));

        return audioDiarizer.diarize(publicUrl);
    }

    private List<WordInfo> transcribe(URI uri, Language language) {
        var settings = settingsLoader.load(GoogleSpeechSettings.class);
        var recognitionConfig = newRecognitionConfig(language, settings);
        var batchRecognizeFileMetadata = BatchRecognizeFileMetadata.newBuilder()
                .setUri(uri.toString())
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

        try(var speechClient = newSpeechClient(googleCloudProperties, settings)) {
            var response = speechClient.batchRecognizeOperationCallable().call(batchRecognizeRequest);
            var singleResponse = response.getResultsMap().get(uri.toString());
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
                    .toList();
        }
    }

    private static RecognitionConfig newRecognitionConfig(Language language, GoogleSpeechSettings settings) {
        Objects.requireNonNull(language);
        Objects.requireNonNull(settings);

        var recognitionFeatures = RecognitionFeatures.newBuilder()
                .setEnableAutomaticPunctuation(true)
                .setEnableWordTimeOffsets(true)
                .build();

        return RecognitionConfig.newBuilder()
                .setAutoDecodingConfig(AutoDetectDecodingConfig.newBuilder().build())
                .addLanguageCodes(language.getBcp47())
                .setModel(settings.getModel())
                .setFeatures(recognitionFeatures)
                .build();
    }

    @SneakyThrows({IOException.class})
    private static SpeechClient newSpeechClient(GoogleCloudProperties properties, GoogleSpeechSettings settings) {
        Objects.requireNonNull(properties);
        Objects.requireNonNull(settings);

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
                .setEndpoint(properties.getSpeechEndpoint());

        settingsBuilder.batchRecognizeOperationSettings()
                .setPollingAlgorithm(OperationTimedPollAlgorithm.create(retrySettings));

        return SpeechClient.create(settingsBuilder.build());
    }

    private static String newImplicitRecognizer(GoogleCloudProperties properties) {
        return RecognizerName.of(ServiceOptions.getDefaultProjectId(), properties.getLocation(), "_").toString();
    }

}
