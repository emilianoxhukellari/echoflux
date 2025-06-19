package echoflux.domain.transcription_word.data;

import echoflux.domain.core.data.BaseProjection;
import org.immutables.value.Value;

@Value.Immutable
public interface ScalarTranscriptionWordProjection extends BaseProjection<Long> {

    String getContent();

    String getSpeakerName();

    Long getStartOffsetMillis();

    Long getEndOffsetMillis();

    Integer getSequence();

}
