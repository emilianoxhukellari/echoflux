package echoflux.core.word.processor;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.Validate;
import echoflux.core.core.utils.MoreStrings;
import echoflux.core.word.common.HasContent;
import echoflux.core.word.common.SimpleContent;
import echoflux.core.word.common.SpeakerSegmentInfo;
import echoflux.core.word.common.SpeakerSegmentPartition;
import echoflux.core.word.common.WordInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class SpeakerSegmentPartitioner {

    public static <S extends SpeakerSegmentInfo<?>> List<SpeakerSegmentPartition> partitionAll(List<S> segments,
                                                                                               int wordLimit) {
        Objects.requireNonNull(segments, "segments");
        Validate.isTrue(wordLimit > 0, "wordLimit must be > 0");

        var partitions = new ArrayList<SpeakerSegmentPartition>();
        var currentContents = new ArrayList<HasContent>();

        for (var segment : segments) {
            if (currentContents.size() + segment.getWords().size() <= wordLimit) {
                currentContents.addAll(segment.getWords());
                currentContents.add(SimpleContent.of(MoreStrings.EMPTY_LINE));
            } else {
                if (!currentContents.isEmpty()) {
                    partitions.add(SpeakerSegmentPartition.ofContents(currentContents));
                    currentContents = new ArrayList<>();
                }

                if (segment.getWords().size() > wordLimit) {
                    partitions.addAll(
                            partitionSingle(segment.getWords(), wordLimit)
                    );
                } else {
                    currentContents.addAll(segment.getWords());
                    currentContents.add(SimpleContent.of(MoreStrings.EMPTY_LINE));
                }
            }
        }

        if (!currentContents.isEmpty()) {
            partitions.add(SpeakerSegmentPartition.ofContents(currentContents));
        }

        return Collections.unmodifiableList(partitions);
    }

    private static List<SpeakerSegmentPartition> partitionSingle(List<? extends WordInfo> segmentWords,
                                                                 int wordLimit) {
        Objects.requireNonNull(segmentWords, "segmentWords");
        Validate.isTrue(wordLimit > 0, "wordLimit must be > 0");

        if (segmentWords.size() <= wordLimit) {
            return List.of(SpeakerSegmentPartition.ofContents(segmentWords));
        }

        int mid = segmentWords.size() / 2;
        var leftWords = segmentWords.subList(0, mid);
        var rightWords = segmentWords.subList(mid, segmentWords.size());

        return ListUtils.union(
                partitionSingle(leftWords, wordLimit),
                partitionSingle(rightWords, wordLimit)
        );
    }

}
