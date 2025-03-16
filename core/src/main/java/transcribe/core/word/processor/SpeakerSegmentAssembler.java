package transcribe.core.word.processor;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import transcribe.core.word.common.SpeakerSegmentInfo;
import transcribe.core.word.common.WordInfo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class SpeakerSegmentAssembler {

    public static <
            W extends WordInfo,
            S extends SpeakerSegmentInfo<W>
            > List<S> assembleAll(List<W> words, Supplier<S> newSegment) {
        Objects.requireNonNull(words, "words");
        Objects.requireNonNull(newSegment, "newSegment");

        if (words.isEmpty()) {
            return List.of();
        }

        var currS = newSegment.get();
        currS.setSpeakerName(words.getFirst().getSpeakerName());
        currS.setStartOffsetMillis(words.getFirst().getStartOffsetMillis());
        currS.setEndOffsetMillis(words.getFirst().getEndOffsetMillis());
        currS.setWords(Lists.newArrayList(words.getFirst()));

        var sList = Lists.newArrayList(currS);

        for (int i = 1; i < words.size(); i++) {
            var w = words.get(i);
            if (StringUtils.equals(currS.getSpeakerName(), w.getSpeakerName())) {
                currS.setEndOffsetMillis(w.getEndOffsetMillis());
                currS.getWords().add(w);
            } else {
                currS = newSegment.get();
                currS.setSpeakerName(w.getSpeakerName());
                currS.setStartOffsetMillis(w.getStartOffsetMillis());
                currS.setEndOffsetMillis(w.getEndOffsetMillis());
                currS.setWords(Lists.newArrayList(w));

                sList.add(currS);
            }
        }

        return Collections.unmodifiableList(sList);
    }

}
