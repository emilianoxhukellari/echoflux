package echoflux.domain.transcription_word.data;

import echoflux.domain.transcription.data.TranscriptionProjection;
import org.immutables.value.Value;

@Value.Immutable
public interface TranscriptionWordProjection extends ScalarTranscriptionWordProjection {

    TranscriptionProjection getTranscription();

}
