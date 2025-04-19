package echoflux.core.word.common;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public record SpeakerSegmentPartition(String content) {

    public static <T extends HasContent> SpeakerSegmentPartition ofContents(List<T> contents) {
        Objects.requireNonNull(contents, "contents");

        var content = contents.stream()
                .map(HasContent::getContent)
                .collect(Collectors.joining(StringUtils.SPACE));

        return new SpeakerSegmentPartition(content);
    }

}
