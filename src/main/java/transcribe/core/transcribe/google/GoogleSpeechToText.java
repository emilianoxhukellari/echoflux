package transcribe.core.transcribe.google;

import transcribe.config.properties.GoogleCloudProperties;
import transcribe.core.common.utils.MoreLists;
import transcribe.core.transcribe.SpeechToText;
import transcribe.core.transcribe.common.Language;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v2.*;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import transcribe.core.transcribe.common.TranscribeResult;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
public class GoogleSpeechToText implements SpeechToText, DisposableBean {

    private final SpeechClient speechClient;
    private final String implicitRecognizer;

    public GoogleSpeechToText(GoogleCloudProperties googleCloudProperties) {
        this.speechClient = newSpeechClient(googleCloudProperties);
        this.implicitRecognizer = newImplicitRecognizer(googleCloudProperties);
    }

    @SneakyThrows
    public TranscribeResult transcribe(URI cloudUri, List<Language> languages) {
        var recognitionConfig = newRecognitionConfig(languages);
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

        var response = speechClient.batchRecognizeAsync(batchRecognizeRequest).get();
        var singleResponse = response.getResultsMap().get(uri);
        var resultsList = singleResponse.getInlineResult().getTranscript().getResultsList();

        return GoogleSpeechToTextUtils.toTranscribeResult(resultsList);
    }

    @Override
    public void destroy() {
        speechClient.close();
    }

    private static RecognitionConfig newRecognitionConfig(List<Language> languages) {
        var recognitionFeatures = RecognitionFeatures.newBuilder()
                .setEnableAutomaticPunctuation(true)
                .setEnableWordTimeOffsets(true)
                .build();

        return RecognitionConfig.newBuilder()
                .setAutoDecodingConfig(AutoDetectDecodingConfig.newBuilder().build())
                .addAllLanguageCodes(MoreLists.collect(languages, Language::getBcp47))
                .setModel("chirp")
                .setFeatures(recognitionFeatures)
                .build();
    }

    @SneakyThrows
    private static SpeechClient newSpeechClient(GoogleCloudProperties properties) {
        @Cleanup
        var privateKeyStream = IOUtils.toInputStream(properties.getPrivateKey(), StandardCharsets.UTF_8);
        var credentials = GoogleCredentials.fromStream(privateKeyStream).createScoped(SpeechSettings.getDefaultServiceScopes());

        var speechSettings = SpeechSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .setEndpoint(properties.getSpeechEndpoint())
                .build();

        return SpeechClient.create(speechSettings);
    }

    private static String newImplicitRecognizer(GoogleCloudProperties properties) {
        return RecognizerName.of(properties.getProjectId(), properties.getLocation(), "_").toString();
    }

}
