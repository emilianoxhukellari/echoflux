package transcribe.core.word.processor;

import org.apache.commons.lang3.StringUtils;
import transcribe.core.core.diff.DiffRow;
import transcribe.core.core.diff.DiffRowGenerator;
import transcribe.core.core.diff.DiffTag;
import transcribe.core.core.diff.Equalizer;
import transcribe.core.core.diff.SimilarityEqualizer;
import transcribe.core.core.utils.MoreLists;
import transcribe.core.core.utils.MoreStrings;
import transcribe.core.word.common.WordInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class WordPatcher {

    private static final Equalizer<String> EQUALIZER = new SimilarityEqualizer(0.8);

    public static <T extends WordInfo> List<T> patchAll(List<T> original, String revised, Supplier<T> newWord) {
        Objects.requireNonNull(original, "original");
        Objects.requireNonNull(revised, "revised");
        Objects.requireNonNull(newWord, "newWord");

        var rows = generateDiffRows(original, revised);
        var patchedWords = new ArrayList<T>(rows.size());

        int w = 0;

        for (int i = 0; i < rows.size(); i++) {
            var row = rows.get(i);

            switch (row.tag()) {
                case EQUAL, SIMILAR, CHANGE -> {
                    var word = original.get(w);
                    word.setContent(row.newLine());
                    patchedWords.add(word);
                    w++;
                }
                case DELETE -> w++;
                case INSERT -> {
                    var seqInserts = new ArrayList<String>();
                    do {
                        seqInserts.add(rows.get(i).newLine());
                        i++;
                    } while (i < rows.size() && rows.get(i).tag() == DiffTag.INSERT);
                    i--;

                    T anchor;
                    if ((anchor = MoreLists.getSafe(patchedWords, patchedWords.size() - 1)) != null) {
                        patchedWords.addAll(
                                applyLeftAnchor(anchor, seqInserts, newWord)
                        );
                    } else if ((anchor = MoreLists.getSafe(original, w)) != null) {
                        patchedWords.addAll(
                                applyRightAnchor(anchor, seqInserts, newWord)
                        );
                    } else {
                        patchedWords.addAll(
                                applyNoAnchor(seqInserts, newWord)
                        );
                    }
                }
            }
        }

        return Collections.unmodifiableList(patchedWords);
    }

    private static <T extends WordInfo> List<T> applyLeftAnchor(T anchor, List<String> inserts, Supplier<T> newWord) {
        double timePerChar = findTimeMillisPerChar(anchor, inserts);
        long insertEndOffsetLimit = anchor.getEndOffsetMillis();
        long newAnchorEndOffset = anchor.getStartOffsetMillis() + (long) (timePerChar * anchor.getContent().length());
        anchor.setEndOffsetMillis(newAnchorEndOffset);

        return buildInsertWords(
                inserts,
                anchor.getSpeakerName(),
                timePerChar,
                newAnchorEndOffset,
                insertEndOffsetLimit,
                newWord
        );
    }

    private static <T extends WordInfo> List<T> applyRightAnchor(T anchor, List<String> inserts, Supplier<T> newWord) {
        double timePerChar = findTimeMillisPerChar(anchor, inserts);
        long insertStartOffsetLimit = anchor.getStartOffsetMillis();
        long newAnchorStartOffset = anchor.getEndOffsetMillis() - (long) (timePerChar * anchor.getContent().length());
        anchor.setStartOffsetMillis(newAnchorStartOffset);

        return buildInsertWords(
                inserts,
                anchor.getSpeakerName(),
                timePerChar,
                insertStartOffsetLimit,
                newAnchorStartOffset,
                newWord
        );
    }

    private static <T extends WordInfo> List<T> buildInsertWords(List<String> inserts,
                                                                 String speakerName,
                                                                 double timePerChar,
                                                                 long startOffset,
                                                                 long endOffsetLimit,
                                                                 Supplier<T> newWord) {
        var words = new ArrayList<T>(inserts.size());

        for (var insert : inserts) {
            long endOffset = Math.min(endOffsetLimit, startOffset + (long) (timePerChar * insert.length()));

            var word = newWord.get();
            word.setContent(insert);
            word.setStartOffsetMillis(startOffset);
            word.setEndOffsetMillis(endOffset);
            word.setSpeakerName(speakerName);

            words.add(word);

            startOffset = endOffset;
        }

        return Collections.unmodifiableList(words);
    }

    private static <T extends WordInfo> List<T> applyNoAnchor(List<String> inserts, Supplier<T> newWord) {
        return inserts.stream()
                .map(insert -> {
                            var word = newWord.get();
                            word.setContent(insert);
                            word.setStartOffsetMillis(0L);
                            word.setEndOffsetMillis(0L);
                            word.setSpeakerName("Unknown");

                            return word;
                        }
                )
                .toList();
    }

    private static <T extends WordInfo> double findTimeMillisPerChar(T anchor, List<String> inserts) {
        long duration = anchor.getEndOffsetMillis() - anchor.getStartOffsetMillis();
        long charCount = MoreStrings.countChars(inserts) + anchor.getContent().length();

        if (charCount == 0) {
            return 0;
        }

        return (double) duration / charCount;
    }

    private static <T extends WordInfo> List<DiffRow> generateDiffRows(List<T> original, String revised) {
        var originalTokens = MoreLists.collect(original, T::getContent);
        var revisedTokens = List.of(StringUtils.split(revised));

        return DiffRowGenerator.generate(originalTokens, revisedTokens, EQUALIZER);
    }

}
