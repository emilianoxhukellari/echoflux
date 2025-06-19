package echoflux.core.word.processor;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import echoflux.core.word.common.SpeakerSegmentInfo;
import echoflux.core.word.common.WordInfo;

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

        var currSegment = newSegment.get();
        var currContentBuilder = new StringBuilder(words.getFirst().getContent());

        currSegment.setSpeakerName(words.getFirst().getSpeakerName());
        currSegment.setStartOffsetMillis(words.getFirst().getStartOffsetMillis());
        currSegment.setEndOffsetMillis(words.getFirst().getEndOffsetMillis());
        currSegment.setWords(Lists.newArrayList(words.getFirst()));

        var segmentList = Lists.newArrayList(currSegment);

        for (int i = 1; i < words.size(); i++) {
            var word = words.get(i);
            if (StringUtils.equals(currSegment.getSpeakerName(), word.getSpeakerName())) {
                currSegment.setEndOffsetMillis(word.getEndOffsetMillis());
                currSegment.getWords().add(word);
                currContentBuilder.append(StringUtils.SPACE).append(word.getContent());
            } else {
                currSegment.setContent(currContentBuilder.toString());

                currSegment = newSegment.get();
                currContentBuilder = new StringBuilder(word.getContent());

                currSegment.setSpeakerName(word.getSpeakerName());
                currSegment.setStartOffsetMillis(word.getStartOffsetMillis());
                currSegment.setEndOffsetMillis(word.getEndOffsetMillis());
                currSegment.setWords(Lists.newArrayList(word));

                segmentList.add(currSegment);
            }
        }

        currSegment.setContent(currContentBuilder.toString());

        return Collections.unmodifiableList(segmentList);
    }

}
