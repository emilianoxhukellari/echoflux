package transcribe.application.core.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.internal.JsonUtils;
import org.apache.commons.lang3.Validate;
import transcribe.application.core.spring.SpringContext;
import transcribe.core.cloud_storage.CloudStorage;
import transcribe.core.cloud_storage.GetSignedUrlOfUriCommand;
import transcribe.domain.transcription.data.TranscriptionProjection;
import transcribe.domain.transcription.manager.TranscriptionManager;
import transcribe.domain.transcription.service.TranscriptionService;
import transcribe.domain.transcription_word.data.SpeakerSegmentDto;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Tag("audio-text-connector")
@JsModule("./element/audio-text-connector/audio-text-connector.ts")
public class AudioTextConnector extends Component implements HasSize {

    private final TranscriptionManager transcriptionManager;
    private final CloudStorage cloudStorage;
    private final TranscriptionProjection transcription;

    public AudioTextConnector(Long transcriptionId) {
        Objects.requireNonNull(transcriptionId, "transcriptionId cannot be null");

        this.transcriptionManager = SpringContext.getBean(TranscriptionManager.class);
        this.cloudStorage = SpringContext.getBean(CloudStorage.class);
        this.transcription = SpringContext.getBean(TranscriptionService.class)
                .projectById(transcriptionId);

        build();
    }

    public void build() {
        var audioUrl = cloudStorage.getSignedUrl(
                GetSignedUrlOfUriCommand.builder()
                        .cloudUri(transcription.cloudUri())
                        .duration(Duration.ofHours(8))
                        .build()
        );

        var speakerSegments = transcriptionManager.getTranscriptionSpeakerSegments(transcription.id());

        setAudioUrl(audioUrl.toString());
        setSpeakerWordSegments(speakerSegments);
    }

    public void setAudioUrl(String audioUrl) {
        Validate.notBlank(audioUrl, "audioSrc cannot be blank");

        getElement().setProperty("audioSrc", audioUrl);
    }

    public void setSpeakerWordSegments(List<SpeakerSegmentDto> speakerSegments) {
        Objects.requireNonNull(speakerSegments, "speakerSegments cannot be null");

        var jsonValue = JsonUtils.listToJson(speakerSegments);

        getElement().setPropertyJson("speakerSegments", jsonValue);
    }

    public void setMaxHighlightedWords(int maxHighlightedWords) {
        Validate.isTrue(maxHighlightedWords > 0, "maxHighlightedWords must be greater than 0");

        getElement().setProperty("maxHighlightedWords", maxHighlightedWords);
    }

    private void pause() {
        getElement().callJsFunction("pause");
    }

}