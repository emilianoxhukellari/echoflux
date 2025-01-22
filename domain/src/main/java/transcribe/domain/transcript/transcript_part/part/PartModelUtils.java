package transcribe.domain.transcript.transcript_part.part;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import transcribe.core.core.iterable.DoublyLinkedIterable;
import transcribe.core.core.iterable.DoublyLinkedNode;
import transcribe.core.core.utils.MoreStrings;
import transcribe.core.transcribe.common.SpeechToTextWord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public final class PartModelUtils {

    private static final Pattern PATTERN = Pattern.compile("(?s)\\[(\\d+)]\\s*(.*?)(?=\\[\\d+]|$)");

    public static List<PartModel> toParts(List<SpeechToTextWord> speechToTextWords, int sequenceStart, int maxWordsPerPart) {
        Objects.requireNonNull(speechToTextWords, "speechToTextWords must not be null");
        Validate.isTrue(maxWordsPerPart > 0, "maxWordsPerPart must be greater than 0");

        var parts = new ArrayList<PartModel>();
        var totalWords = speechToTextWords.size();

        for (int fromIndex = 0, sequence = sequenceStart; fromIndex < totalWords; fromIndex += maxWordsPerPart, sequence++) {
            var toIndex = Math.min(fromIndex + maxWordsPerPart, totalWords);
            var partWords = speechToTextWords.subList(fromIndex, toIndex);

            var startOffset = partWords.getFirst()
                    .getStartOffsetMillis();
            var endOffset = (toIndex < totalWords)
                    ? speechToTextWords.get(toIndex).getStartOffsetMillis()
                    : partWords.getLast().getEndOffsetMillis();

            var text = partWords.stream()
                    .map(SpeechToTextWord::getContent)
                    .collect(Collectors.joining(StringUtils.SPACE));

            var stripped = MoreStrings.stripSpace(text);

            parts.add(
                    PartModel.builder()
                            .text(stripped)
                            .startOffsetMillis(startOffset)
                            .endOffsetMillis(endOffset)
                            .sequence(sequence)
                            .endOfPartition(toIndex >= totalWords)
                            .build()
            );
        }

        return parts;
    }

    public static List<PartModel> parse(String textWithMetadata, List<PartModel> originalPartModels) {
        if (StringUtils.isBlank(textWithMetadata) || CollectionUtils.isEmpty(originalPartModels)) {
            return List.of();
        }

        var matcher = PATTERN.matcher(textWithMetadata);
        var parsedParts = new DoublyLinkedIterable<PartModel>();

        while (matcher.find()) {
            var sequence = Integer.parseInt(matcher.group(1));
            var text = matcher.group(2);
            var stripped = MoreStrings.stripSpace(text);

            parsedParts.add(
                    PartModel.builder()
                            .sequence(sequence)
                            .text(stripped)
                            .build()
            );
        }

        var originalPartByIndex = originalPartModels.stream()
                .collect(Collectors.toMap(PartModel::getSequence, Function.identity()));

        for (var parsedPart : parsedParts) {
            Validate.notNull(
                    originalPartByIndex.get(parsedPart.getValue().getSequence()),
                    "Original part not found for parsed part: %s".formatted(parsedPart.getValue())
            );
        }

        var validParsedParts = parsedParts.nodeStream()
                .filter(p -> isValidParsedPart(p, originalPartModels.getLast()))
                .map(DoublyLinkedNode::getValue)
                .map(p -> PartModel.builder()
                        .sequence(p.getSequence())
                        .text(p.getText())
                        .startOffsetMillis(originalPartByIndex.get(p.getSequence()).getStartOffsetMillis())
                        .endOffsetMillis(originalPartByIndex.get(p.getSequence()).getEndOffsetMillis())
                        .endOfPartition(originalPartByIndex.get(p.getSequence()).getEndOfPartition())
                        .build()
                )
                .collect(Collectors.toMap(PartModel::getSequence, Function.identity()));

        return IntStream.range(0, originalPartModels.size())
                .boxed()
                .map(i -> Objects.requireNonNullElseGet(validParsedParts.get(i), () -> originalPartByIndex.get(i)))
                .toList();
    }

    public static String toText(List<PartModel> partModels, boolean withMetadata) {
        Objects.requireNonNull(partModels, "parts must not be null");

        Iterator<String> iterator;

        if (withMetadata) {
            iterator = partModels.stream()
                    .map(p -> "[%d] %s".formatted(p.getSequence(), p.getText()))
                    .iterator();
        } else {
            iterator = partModels.stream()
                    .map(PartModel::getText)
                    .iterator();
        }

        return toText(iterator);
    }

    private static boolean isValidParsedPart(DoublyLinkedNode<PartModel> parsedPart, PartModel lastOriginalPart) {
        Objects.requireNonNull(parsedPart, "parsedPart must not be null");
        Objects.requireNonNull(lastOriginalPart, "lastOriginalPart must not be null");

        if (Objects.equals(parsedPart.getValue().getSequence(), lastOriginalPart.getSequence())) {
            return true;
        }

        if (parsedPart.getNext() == null) {
            return false;
        }

        return Objects.equals(parsedPart.getNext().getValue().getSequence(), parsedPart.getValue().getSequence() + 1);
    }

    private static String toText(Iterator<String> iterator) {
        Objects.requireNonNull(iterator, "iterator must not be null");

        var sb = new StringBuilder();

        while (iterator.hasNext()) {
            var current = iterator.next();
            sb.append(current);

            if (iterator.hasNext() && !current.endsWith(StringUtils.LF)) {
                sb.append(StringUtils.SPACE);
            }
        }

        return sb.toString();
    }

}
