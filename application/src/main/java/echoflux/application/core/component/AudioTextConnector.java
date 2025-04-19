package echoflux.application.core.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.internal.AllowInert;
import com.vaadin.flow.dom.DisabledUpdateMode;
import com.vaadin.flow.internal.JsonUtils;
import elemental.json.JsonArray;
import org.apache.commons.lang3.Validate;
import echoflux.application.core.operation.Operation;
import echoflux.application.core.operation.OperationCallable;
import echoflux.application.core.operation.OperationRunner;
import echoflux.application.transcribe.DownloadTranscriptDialog;
import echoflux.core.cloud_storage.CloudStorage;
import echoflux.core.cloud_storage.GetSignedUrlOfUriCommand;
import echoflux.core.core.bean.loader.BeanLoader;
import echoflux.core.word.processor.SpeakerSegmentAssembler;
import echoflux.core.word.processor.WordPatcher;
import echoflux.domain.operation.data.OperationType;
import echoflux.domain.transcription.data.TranscriptionProjection;
import echoflux.domain.transcription.manager.TranscriptionManager;
import echoflux.domain.transcription.service.TranscriptionService;
import echoflux.domain.transcription_word.data.SpeakerSegmentDto;
import echoflux.domain.transcription_word.data.WordDto;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Tag("audio-text-connector")
@JsModule("./element/audio-text-connector/audio-text-connector.ts")
public class AudioTextConnector extends Component implements HasSize, HasComponents {

    private final HelperDownloadAnchor.Factory helperDownloadAnchorFactory;
    private final BeanLoader beanLoader;
    private final TranscriptionManager transcriptionManager;
    private final CloudStorage cloudStorage;
    private final OperationRunner operationRunner;
    private final TranscriptionProjection transcription;
    private final List<WordDto> wordsState = new ArrayList<>();

    public AudioTextConnector(Long transcriptionId, BeanLoader beanLoader) {
        Objects.requireNonNull(transcriptionId, "transcriptionId");
        Objects.requireNonNull(beanLoader, "beanLoader");

        this.beanLoader = beanLoader;
        this.transcriptionManager = beanLoader.load(TranscriptionManager.class);
        this.cloudStorage = beanLoader.load(CloudStorage.class);
        this.operationRunner = beanLoader.load(OperationRunner.class);
        this.transcription = beanLoader.load(TranscriptionService.class)
                .projectById(transcriptionId);
        this.helperDownloadAnchorFactory = HelperDownloadAnchor.newFactory(this);

        build();
    }

    public void build() {
        var audioUrl = cloudStorage.getSignedUrl(
                GetSignedUrlOfUriCommand.builder()
                        .cloudUri(transcription.cloudUri())
                        .duration(Duration.ofHours(8))
                        .build()
        );

        wordsState.addAll(
                transcriptionManager.getTranscriptionSpeakerWords(transcription.id())
        );

        setAudioUrl(audioUrl.toString());
        setSpeakerSegments(SpeakerSegmentAssembler.assembleAll(wordsState, SpeakerSegmentDto::new));
    }

    public void setAudioUrl(String audioUrl) {
        Validate.notBlank(audioUrl, "audioSrc cannot be blank");

        getElement().setProperty("audioSrc", audioUrl);
    }

    public void setSpeakerSegments(List<SpeakerSegmentDto> speakerSegments) {
        Objects.requireNonNull(speakerSegments, "speakerSegments cannot be null");

        var jsonValue = JsonUtils.listToJson(speakerSegments);

        getElement().setPropertyJson("speakerSegments", jsonValue);
    }

    public void setMaxHighlightedWords(int maxHighlightedWords) {
        Validate.isTrue(maxHighlightedWords > 0, "maxHighlightedWords must be greater than 0");

        getElement().setProperty("maxHighlightedWords", maxHighlightedWords);
    }

    @SuppressWarnings("unused")
    @ClientCallable(DisabledUpdateMode.ALWAYS)
    @AllowInert
    private JsonArray saveAllSpeakerSegments(JsonArray speakerSegments) {
        var speakerSegmentsDto = JsonUtils.readValue(speakerSegments, new TypeReference<List<SpeakerSegmentDto>>() {
        });

        var words = WordPatcher.patchAllFromSegments(wordsState, speakerSegmentsDto, WordDto::new);
        wordsState.clear();
        wordsState.addAll(words);

        var segments = SpeakerSegmentAssembler.assembleAll(words, SpeakerSegmentDto::new);

        var saveOperation = Operation.<Void>builder()
                .name("Saving transcription words")
                .description("Transcription with ID [%d]".formatted(transcription.id()))
                .callable(OperationCallable.ofRunnable(() -> transcriptionManager.saveWords(transcription.id(), words)))
                .onSuccessNotify(false)
                .onProgressNotify(false)
                .type(OperationType.NON_BLOCKING)
                .build();

        operationRunner.run(saveOperation, UI.getCurrent());

        return JsonUtils.listToJson(segments);
    }

    @SuppressWarnings("unused")
    @ClientCallable(DisabledUpdateMode.ALWAYS)
    @AllowInert
    private void downloadTranscript() {
        new DownloadTranscriptDialog(transcription.id(), helperDownloadAnchorFactory, beanLoader)
                .open();
    }

}