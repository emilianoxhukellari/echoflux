package transcribe.domain.transcript.transcript_part.part;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import transcribe.core.transcribe.common.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class PartUtils {

    private static final Pattern PATTERN = Pattern.compile("\\[(\\d+)]\\s*([^\\[]+)\\s*\\[(\\d+)]");

    public static List<Part> toParts(List<Word> words, int maxWordsPerPart) {
        Objects.requireNonNull(words, "words must not be null");
        Validate.isTrue(maxWordsPerPart > 0, "maxWordsPerPart must be greater than 0");

        var parts = new ArrayList<Part>();
        var totalWords = words.size();

        for (int fromIndex = 0; fromIndex < totalWords; fromIndex += maxWordsPerPart) {
            var toIndex = Math.min(fromIndex + maxWordsPerPart, totalWords);
            var partWords = words.subList(fromIndex, toIndex);

            var startOffset = partWords.getFirst().getStartOffsetMillis();
            var endOffset = (toIndex < totalWords)
                    ? words.get(toIndex).getStartOffsetMillis()
                    : partWords.getLast().getEndOffsetMillis();

            var text = partWords.stream()
                    .map(Word::getText)
                    .collect(Collectors.joining(StringUtils.SPACE));

            parts.add(
                    Part.builder()
                            .text(text)
                            .startOffsetMillis(startOffset)
                            .endOffsetMillis(endOffset)
                            .build()
            );
        }

        return parts;
    }

    public static List<Part> parse(String textWithTimestamps) {
        if (StringUtils.isBlank(textWithTimestamps)) {
            return List.of(
                    Part.builder()
                            .text(StringUtils.EMPTY)
                            .startOffsetMillis(0)
                            .endOffsetMillis(0)
                            .build()
            );
        }

        var matcher = PATTERN.matcher(textWithTimestamps);
        var parts = new ArrayList<Part>();

        while (matcher.find()) {
            var startOffset = Long.parseLong(matcher.group(1));
            var text = StringUtils.stripToEmpty(matcher.group(2));
            var endOffset = Long.parseLong(matcher.group(3));

            parts.add(
                    Part.builder()
                            .text(text)
                            .startOffsetMillis(startOffset)
                            .endOffsetMillis(endOffset)
                            .build()
            );
        }

        return parts;
    }

    public static String toText(List<Part> parts) {
        return toText(parts, false);
    }

    public static String toTextWithTimestamps(List<Part> parts) {
        return toText(parts, true);
    }

    private static String toText(List<Part> parts, boolean withTimestamps) {
        Objects.requireNonNull(parts, "parts must not be null");

        return parts.stream()
                .map(p ->
                        withTimestamps
                                ? "[%d] %s".formatted(p.getStartOffsetMillis(), p.getText())
                                : p.getText()
                )
                .collect(Collectors.joining(StringUtils.SPACE));
    }

}
