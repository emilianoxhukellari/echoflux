package echoflux.application.core.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.internal.AllowInert;
import com.vaadin.flow.internal.JsonUtils;
import echoflux.core.core.validate.guard.Guard;
import echoflux.domain.jooq.tables.pojos.Transcription;
import echoflux.domain.transcription.service.TranscriptionService;
import echoflux.domain.transcription.data.BaseSpeakerSegment;
import echoflux.domain.transcription.data.SequencedWord;
import elemental.json.JsonArray;
import org.apache.commons.lang3.Validate;
import echoflux.application.core.operation.Operation;
import echoflux.application.core.operation.OperationCallable;
import echoflux.application.transcribe.DownloadTranscriptDialog;
import echoflux.core.storage.Storage;
import echoflux.core.core.bean.accessor.BeanAccessor;
import echoflux.core.word.processor.WordPatcher;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Tag("audio-text-connector")
@JsModule("./element/audio-text-connector/audio-text-connector.ts")
public class AudioTextConnector extends Component implements HasSize, HasComponents {

    private final HelperDownloadAnchor.Factory helperDownloadAnchorFactory;
    private final Transcription transcription;
    private final BeanAccessor beanAccessor;
    private final TranscriptionService transcriptionService;
    private final Storage storage;
    private final List<SequencedWord> wordsState = new ArrayList<>();

    public AudioTextConnector(Transcription transcription, BeanAccessor beanAccessor) {
        Guard.notNull(transcription, "transcription");
        Guard.notNull(beanAccessor, "beanAccessor");

        this.transcription = transcription;
        this.beanAccessor = beanAccessor;
        this.transcriptionService = beanAccessor.get(TranscriptionService.class);
        this.storage = beanAccessor.getStorage(transcription.getStorageProvider());
        this.helperDownloadAnchorFactory = HelperDownloadAnchor.newFactory(this);

        build();
    }

    public void build() {
        var audioUrl = storage.getSignedUrl(transcription.getUri(), Duration.ofHours(8));

        var words = transcriptionService.getTranscriptionWords(transcription.getId());
        wordsState.addAll(words);

        setAudioSrc(audioUrl.toString());
        setWords(wordsState);
    }

    public void setAudioSrc(String audioSrc) {
        Validate.notBlank(audioSrc, "audioSrc cannot be blank");

        getElement().setProperty("audioSrc", audioSrc);
    }

    public void setWords(List<SequencedWord> words) {
        Objects.requireNonNull(words, "words cannot be null");

        var jsonValue = JsonUtils.listToJson(words);

        getElement().setPropertyJson("words", jsonValue);
    }

    public void setMaxHighlightedWords(int maxHighlightedWords) {
        Validate.isTrue(maxHighlightedWords > 0, "maxHighlightedWords must be greater than 0");

        getElement().setProperty("maxHighlightedWords", maxHighlightedWords);
    }

    @SuppressWarnings("unused")
    @ClientCallable
    @AllowInert
    private JsonArray saveAllSpeakerSegments(JsonArray speakerSegments) {
        var baseSpeakerSegments = JsonUtils.readValue(speakerSegments, new TypeReference<List<BaseSpeakerSegment>>() {
        });

        var words = WordPatcher.patchAllFromSegments(wordsState, baseSpeakerSegments, SequencedWord::new);

        for (int i = 0; i < words.size(); i++) {
            words.get(i).setSequence(i);
        }

        wordsState.clear();
        wordsState.addAll(words);

        Operation.<Void>builder()
                .name("Saving transcription words")
                .callable(OperationCallable.ofRunnable(() -> transcriptionService.saveWords(transcription.getId(), words)))
                .onSuccessNotify(false)
                .onProgressNotify(false)
                .build()
                .runBackground();

        return JsonUtils.listToJson(wordsState);
    }

    @SuppressWarnings("unused")
    @ClientCallable
    @AllowInert
    private void downloadTranscript() {
        new DownloadTranscriptDialog(transcription.getId(), helperDownloadAnchorFactory, beanAccessor)
                .open();
    }

}