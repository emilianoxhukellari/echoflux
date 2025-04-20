package echoflux.core.word.processor;

import jakarta.annotation.Nullable;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import echoflux.core.core.diff.DiffRow;
import echoflux.core.core.diff.DiffRowGenerator;
import echoflux.core.core.diff.DiffTag;
import echoflux.core.core.diff.Equalizer;
import echoflux.core.core.diff.SimilarityEqualizer;
import echoflux.core.core.utils.EfLists;
import echoflux.core.word.common.BaseSpeakerSegmentInfo;
import echoflux.core.word.common.BaseWordInfo;
import echoflux.core.word.common.HasContent;
import echoflux.core.word.common.WordInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class WordPatcher {

    private static final Equalizer<String> EQUALIZER = new SimilarityEqualizer(0.8);

    public static <W extends WordInfo, S extends BaseSpeakerSegmentInfo> List<W> patchAllFromSegments(List<W> original,
                                                                                                      List<S> revised,
                                                                                                      Supplier<W> newWord) {
        Objects.requireNonNull(revised, "revised");

        List<BaseWordInfo> revisedWords = new ArrayList<>();

        for (var speakerSegment : revised) {
            var contents = StringUtils.split(speakerSegment.getContent());

            for (var content : contents) {
                var word = newWord.get();
                word.setContent(content);
                word.setSpeakerName(speakerSegment.getSpeakerName());

                revisedWords.add(word);
            }
        }

        return patchAllFromWords(original, revisedWords, newWord);
    }

    public static <W extends WordInfo, B extends BaseWordInfo> List<W> patchAllFromWords(List<W> original,
                                                                                         List<B> revised,
                                                                                         Supplier<W> newWord) {
        return patchAll(original, revised, newWord, true);
    }

    public static <T extends WordInfo> List<T> patchAllFromText(List<T> original, String revised, Supplier<T> newWord) {
        Objects.requireNonNull(revised, "revised");
        Objects.requireNonNull(newWord, "newWord");

        var revisedWords = StringUtils.split(revised);
        var revisedBaseWords = Arrays.stream(revisedWords)
                .map(content -> {
                    var word = newWord.get();
                    word.setContent(content);

                    return word;
                })
                .toList();

        return patchAll(original, revisedBaseWords, newWord, false);
    }

    private static <W extends WordInfo, B extends BaseWordInfo> List<W> patchAll(List<W> original,
                                                                                 List<B> revised,
                                                                                 Supplier<W> newWord,
                                                                                 boolean useRevisedSpeaker) {
        Objects.requireNonNull(original, "original");
        Objects.requireNonNull(revised, "revised");
        Objects.requireNonNull(newWord, "newWord");

        var rows = generateDiffRows(original, revised);
        var patchedWords = new ArrayList<W>(rows.size());

        int originalIndex = 0;
        int revisedIndex = 0;

        for (int i = 0; i < rows.size(); i++) {
            var row = rows.get(i);

            switch (row.tag()) {
                case EQUAL, SIMILAR, CHANGE -> {
                    var content = row.newLine();
                    var originalWord = original.get(originalIndex);

                    var speakerName = useRevisedSpeaker ? revised.get(revisedIndex).getSpeakerName() : originalWord.getSpeakerName();
                    originalWord.setContent(content);
                    originalWord.setSpeakerName(speakerName);

                    patchedWords.add(originalWord);
                    originalIndex++;
                    revisedIndex++;
                }
                case DELETE -> originalIndex++;
                case INSERT -> {
                    var seqInserts = new ArrayList<WordInsert>();
                    do {
                        var content = rows.get(i).newLine();
                        var wordInsert = WordInsert.builder()
                                .content(content)
                                .speakerName(useRevisedSpeaker ? revised.get(revisedIndex).getSpeakerName() : null)
                                .build();
                        seqInserts.add(wordInsert);
                        i++;
                        revisedIndex++;
                    } while (i < rows.size() && rows.get(i).tag() == DiffTag.INSERT);
                    i--;

                    W leftAnchor = EfLists.getSafe(patchedWords, patchedWords.size() - 1);
                    W rightAnchor = EfLists.getSafe(original, originalIndex);

                    patchedWords.addAll(
                            applyAnchor(leftAnchor, rightAnchor, seqInserts, newWord, useRevisedSpeaker)
                    );
                }
            }
        }

        return Collections.unmodifiableList(patchedWords);
    }

    private static <T extends WordInfo> List<T> applyAnchor(@Nullable T leftAnchor,
                                                            @Nullable T rightAnchor,
                                                            List<WordInsert> inserts,
                                                            Supplier<T> newWord,
                                                            boolean useRevisedSpeaker) {
        if (leftAnchor == null && rightAnchor == null) {
            return applyNoAnchor(inserts, newWord, useRevisedSpeaker ? WordInsert::speakerName : _ -> "Unknown");
        }

        if (leftAnchor != null && rightAnchor == null) {
            return applyLeftAnchor(leftAnchor, inserts, newWord, useRevisedSpeaker);
        }

        if (leftAnchor == null) {
            return applyRightAnchor(rightAnchor, inserts, newWord, useRevisedSpeaker);
        }

        if (useRevisedSpeaker) {
            var targetSpeakerName = inserts.stream()
                    .map(WordInsert::speakerName)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElseThrow();

            if (Objects.equals(rightAnchor.getSpeakerName(), targetSpeakerName)) {
                return applyRightAnchor(rightAnchor, inserts, newWord, true);
            }

            return applyLeftAnchor(leftAnchor, inserts, newWord, true);
        }

        return applyLeftAnchor(leftAnchor, inserts, newWord, false);
    }

    private static <T extends WordInfo> List<T> applyLeftAnchor(T anchor,
                                                                List<WordInsert> inserts,
                                                                Supplier<T> newWord,
                                                                boolean useRevisedSpeaker) {
        double timePerChar = findTimeMillisPerChar(anchor, inserts);
        long insertEndOffsetLimit = anchor.getEndOffsetMillis();
        long newAnchorEndOffset = anchor.getStartOffsetMillis() + (long) (timePerChar * anchor.getContent().length());
        anchor.setEndOffsetMillis(newAnchorEndOffset);

        return buildInsertWords(
                inserts,
                timePerChar,
                newAnchorEndOffset,
                insertEndOffsetLimit,
                newWord,
                useRevisedSpeaker ? WordInsert::speakerName : _ -> anchor.getSpeakerName()
        );
    }

    private static <T extends WordInfo> List<T> applyRightAnchor(T anchor,
                                                                 List<WordInsert> inserts,
                                                                 Supplier<T> newWord,
                                                                 boolean useRevisedSpeaker) {
        double timePerChar = findTimeMillisPerChar(anchor, inserts);
        long insertStartOffsetLimit = anchor.getStartOffsetMillis();
        long newAnchorStartOffset = anchor.getEndOffsetMillis() - (long) (timePerChar * anchor.getContent().length());
        anchor.setStartOffsetMillis(newAnchorStartOffset);

        return buildInsertWords(
                inserts,
                timePerChar,
                insertStartOffsetLimit,
                newAnchorStartOffset,
                newWord,
                useRevisedSpeaker ? WordInsert::speakerName : _ -> anchor.getSpeakerName()
        );
    }

    private static <T extends WordInfo> List<T> applyNoAnchor(List<WordInsert> inserts,
                                                              Supplier<T> newWord,
                                                              Function<WordInsert, String> toSpeakerName) {
        return inserts.stream()
                .map(insert -> {
                            var word = newWord.get();
                            word.setContent(insert.content());
                            word.setStartOffsetMillis(0L);
                            word.setEndOffsetMillis(0L);
                            word.setSpeakerName(toSpeakerName.apply(insert));

                            return word;
                        }
                )
                .toList();
    }

    private static <T extends WordInfo> List<T> buildInsertWords(List<WordInsert> inserts,
                                                                 double timePerChar,
                                                                 long startOffset,
                                                                 long endOffsetLimit,
                                                                 Supplier<T> newWord,
                                                                 Function<WordInsert, String> toSpeakerName) {
        var words = new ArrayList<T>(inserts.size());

        for (var insert : inserts) {
            long endOffset = Math.min(endOffsetLimit, startOffset + (long) (timePerChar * insert.content().length()));

            var word = newWord.get();
            word.setContent(insert.content());
            word.setStartOffsetMillis(startOffset);
            word.setEndOffsetMillis(endOffset);
            word.setSpeakerName(toSpeakerName.apply(insert));

            words.add(word);

            startOffset = endOffset;
        }

        return Collections.unmodifiableList(words);
    }

    private static <T extends WordInfo> double findTimeMillisPerChar(T anchor, List<WordInsert> inserts) {
        long duration = anchor.getEndOffsetMillis() - anchor.getStartOffsetMillis();
        long insertCharCount = inserts.stream()
                .mapToLong(insert -> insert.content().length())
                .sum();
        long charCount = insertCharCount + anchor.getContent().length();

        if (charCount == 0) {
            return 0;
        }

        return (double) duration / charCount;
    }

    private static <W extends WordInfo, C extends HasContent> List<DiffRow> generateDiffRows(List<W> original, List<C> revised) {
        var originalTokens = EfLists.collect(original, W::getContent);
        var revisedTokens = EfLists.collect(revised, C::getContent);

        return DiffRowGenerator.generate(originalTokens, revisedTokens, EQUALIZER);
    }

    @Builder
    private record WordInsert(String content, String speakerName) {
    }

}
