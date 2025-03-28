package transcribe.core.word.common;

import java.util.List;

public interface SpeakerSegmentInfo<T extends WordInfo> extends BaseSpeakerSegmentInfo, HasOffsets {

    List<T> getWords();

    void setWords(List<T> words);

}

