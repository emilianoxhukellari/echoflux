package transcribe.core.word.common;

public interface HasOffsets {

    Long getStartOffsetMillis();

    Long getEndOffsetMillis();

    void setStartOffsetMillis(Long startOffsetMillis);

    void setEndOffsetMillis(Long endOffsetMillis);

}
