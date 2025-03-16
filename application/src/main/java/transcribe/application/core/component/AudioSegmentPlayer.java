package transcribe.application.core.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import org.apache.commons.lang3.Validate;

import java.util.Objects;

@Tag("audio-segment-player")
@JsModule("./element/audio-segment-player/audio-segment-player.ts")
public class AudioSegmentPlayer extends Component implements HasSize {

    public AudioSegmentPlayer(String audioSrc, Long startOffsetMillis, Long endOffsetMillis) {
        Validate.notBlank(audioSrc, "audioSrc cannot be blank");
        Objects.requireNonNull(startOffsetMillis, "startOffsetMillis cannot be null");
        Objects.requireNonNull(endOffsetMillis, "endOffsetMillis cannot be null");

        getElement().setProperty("audioSrc", audioSrc);
        getElement().setProperty("startOffsetMillis", startOffsetMillis);
        getElement().setProperty("endOffsetMillis", endOffsetMillis);
    }

}