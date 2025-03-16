package transcribe.core.word.common;

public interface WordInfo extends HasContent {

    String getContent();

    String getSpeakerName();

    Long getStartOffsetMillis();

    Long getEndOffsetMillis();

    void setContent(String content);

    void setSpeakerName(String speakerName);

    void setStartOffsetMillis(Long startOffsetMillis);

    void setEndOffsetMillis(Long endOffsetMillis);

}
