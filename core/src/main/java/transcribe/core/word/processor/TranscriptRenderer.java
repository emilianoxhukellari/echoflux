package transcribe.core.word.processor;

import org.apache.commons.lang3.time.DurationFormatUtils;
import transcribe.core.document.Paragraph;
import transcribe.core.word.common.SpeakerSegmentInfo;
import transcribe.core.word.common.WordInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class TranscriptRenderer {

    public static <W extends WordInfo, S extends SpeakerSegmentInfo<W>> List<Paragraph> render(List<S> speakerSegments,
                                                                                               boolean withTimestamps) {
        Objects.requireNonNull(speakerSegments, "speakerSegments");

        var paragraphs = new ArrayList<Paragraph>();
        var itr = speakerSegments.iterator();

        while (itr.hasNext()) {
            var segment = itr.next();
            var speakerNameParagraph = getSpeakerNameParagraph(segment, withTimestamps);
            paragraphs.add(speakerNameParagraph);

            var contentParagraph = Paragraph.of(segment.getContent());
            paragraphs.add(contentParagraph);

            if (itr.hasNext()) {
                paragraphs.add(Paragraph.empty());
            }
        }

        return Collections.unmodifiableList(paragraphs);
    }

    private static Paragraph getSpeakerNameParagraph(SpeakerSegmentInfo<?> segment, boolean withTimestamps) {
        Objects.requireNonNull(segment, "segment");

        if (withTimestamps) {
            var start = DurationFormatUtils.formatDuration(segment.getStartOffsetMillis(), "HH:mm:ss");
            var content = "%s - (%s)".formatted(segment.getSpeakerName(), start);

            return Paragraph.of(content);
        }

        return Paragraph.of(segment.getSpeakerName());
    }

}
