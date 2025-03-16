package transcribe.core.word.common;

import java.util.List;

public interface SpeakerSegmentInfo<T extends WordInfo> {

    String getSpeakerName();

    Long getStartOffsetMillis();

    Long getEndOffsetMillis();

    List<T> getWords();

    void setSpeakerName(String speakerName);

    void setStartOffsetMillis(Long startOffsetMillis);

    void setEndOffsetMillis(Long endOffsetMillis);

    void setWords(List<T> words);

}

