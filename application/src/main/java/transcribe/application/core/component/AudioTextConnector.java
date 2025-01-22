package transcribe.application.core.component;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.internal.AllowInert;
import elemental.json.Json;
import elemental.json.JsonObject;
import org.apache.commons.lang3.Validate;
import transcribe.application.core.spring.SpringContext;
import transcribe.application.transcribe.EditTranscriptPartDialog;
import transcribe.domain.transcript.transcript_part.mapper.TranscriptPartMapper;
import transcribe.domain.transcript.transcript_part.part.PartModel;
import transcribe.domain.transcript.transcript_part.service.TranscriptPartService;

import java.util.List;
import java.util.Objects;

@Tag("audio-text-connector")
@JsModule("./element/audio-text-connector.ts")
public class AudioTextConnector extends Component implements HasSize {

    private final Long transcriptionId;
    private final String audioSrc;

    public AudioTextConnector(Long transcriptionId, String audioSrc, List<PartModel> parts) {
        Objects.requireNonNull(transcriptionId, "transcriptionId cannot be null");
        Validate.notBlank(audioSrc, "audioSrc cannot be blank");

        this.transcriptionId = transcriptionId;
        this.audioSrc = audioSrc;
        getElement().setProperty("audioSrc", audioSrc);
        setAudioParts(parts);
    }

    private void setAudioParts(List<PartModel> parts) {
        Objects.requireNonNull(parts, "parts cannot be null");

        var jsonArray = Json.createArray();

        for (var part : parts) {
            var jsonObject = toJsonObject(part);
            jsonArray.set(jsonArray.length(), jsonObject);
        }

        getElement().setPropertyJson("partModels", jsonArray);
    }

    private static JsonObject toJsonObject(PartModel part) {
        var jsonObject = Json.createObject();
        jsonObject.put("text", part.getText());
        jsonObject.put("sequence", part.getSequence());
        jsonObject.put("startOffsetMillis", part.getStartOffsetMillis());
        jsonObject.put("endOffsetMillis", part.getEndOffsetMillis());

        return jsonObject;
    }

    private void updatePart(PartModel part) {
        Objects.requireNonNull(part);

        var jsonObject = toJsonObject(part);
        getElement().callJsFunction("updatePart", jsonObject);
    }

    private void pause() {
        getElement().callJsFunction("pause");
    }

    @ClientCallable
    @AllowInert
    private void onEditPart(Integer sequence) {
        Objects.requireNonNull(sequence);

        new EditTranscriptPartDialog(transcriptionId, sequence, audioSrc)
                .setSaveListener(_ -> {
                            var transcriptPartEntity = SpringContext.getBean(TranscriptPartService.class)
                                    .getForTranscriptionAndSequence(transcriptionId, sequence);

                            var transcriptPartModel = SpringContext.getBean(TranscriptPartMapper.class)
                                    .toModel(transcriptPartEntity);

                            updatePart(transcriptPartModel);
                        }
                )
                .open();

        pause();
    }

}