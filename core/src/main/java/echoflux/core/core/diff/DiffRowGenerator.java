package echoflux.core.core.diff;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.ChangeDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.DeleteDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.InsertDelta;
import com.github.difflib.patch.Patch;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class DiffRowGenerator {

    private final static Equalizer<String> STRICT_EQUALIZER = new StrictEqualizer();

    public static List<DiffRow> generate(List<String> original, List<String> revised, Equalizer<String> equalizer) {
        Objects.requireNonNull(original, "original");
        Objects.requireNonNull(revised, "revised");
        Objects.requireNonNull(equalizer, "equalizer");

        var patch = DiffUtils.diff(original, revised, equalizer::equals);

        return generate(original, revised, patch);
    }

    public static List<DiffRow> generate(List<String> original, List<String> revised, Patch<String> patch) {
        Objects.requireNonNull(original, "original");
        Objects.requireNonNull(revised, "revised");
        Objects.requireNonNull(patch, "patch");

        var diffRows = new ArrayList<DiffRow>();
        var indexPair = new IndexPair(0, 0);

        for (var delta : patch.getDeltas()) {
            for (var decompressed : decompressDeltas(delta)) {
                indexPair = addRows(original, revised, diffRows, decompressed, indexPair);
            }
        }

        Validate.isTrue(original.size() - indexPair.o == revised.size() - indexPair.r);
        addEqualOrSimilarRows(original, revised, diffRows, original.size() - indexPair.o, indexPair);

        return diffRows;
    }

    private static IndexPair addRows(List<String> original,
                                     List<String> revised,
                                     List<DiffRow> diffRows,
                                     AbstractDelta<String> delta,
                                     IndexPair indexPair) {
        Chunk<String> orig = delta.getSource();
        Chunk<String> rev = delta.getTarget();

        Validate.isTrue(orig.getPosition() - indexPair.o == rev.getPosition() - indexPair.r);
        addEqualOrSimilarRows(original, revised, diffRows, orig.getPosition() - indexPair.o, indexPair);

        switch (delta.getType()) {
            case INSERT -> {
                for (var line : rev.getLines()) {
                    diffRows.add(DiffRow.newInsert(line));
                }
            }
            case DELETE -> {
                for (var line : orig.getLines()) {
                    diffRows.add(DiffRow.newDelete(line));
                }
            }
            default -> {
                for (int i = 0; i < Math.max(orig.size(), rev.size()); i++) {
                    var oldLine = orig.getLines().size() > i ? orig.getLines().get(i) : StringUtils.EMPTY;
                    var newLine = rev.getLines().size() > i ? rev.getLines().get(i) : StringUtils.EMPTY;

                    diffRows.add(DiffRow.newChange(oldLine, newLine));
                }
            }
        }

        return new IndexPair(orig.last() + 1, rev.last() + 1);
    }

    private static List<AbstractDelta<String>> decompressDeltas(AbstractDelta<String> delta) {
        if (delta.getType() == DeltaType.CHANGE && delta.getSource().size() != delta.getTarget().size()) {
            var deltas = new ArrayList<AbstractDelta<String>>();

            int minSize = Math.min(delta.getSource().size(), delta.getTarget().size());
            var oChunk = delta.getSource();
            var rChunk = delta.getTarget();

            deltas.add(
                    new ChangeDelta<>(
                            new Chunk<>(oChunk.getPosition(), oChunk.getLines().subList(0, minSize)),
                            new Chunk<>(rChunk.getPosition(), rChunk.getLines().subList(0, minSize))
                    )
            );

            if (oChunk.getLines().size() < rChunk.getLines().size()) {
                deltas.add(
                        new InsertDelta<>(
                                new Chunk<>(oChunk.getPosition() + minSize, Collections.emptyList()),
                                new Chunk<>(rChunk.getPosition() + minSize, rChunk.getLines().subList(minSize, rChunk.getLines().size()))
                        )
                );
            } else {
                deltas.add(
                        new DeleteDelta<>(
                                new Chunk<>(oChunk.getPosition() + minSize, oChunk.getLines().subList(minSize, oChunk.getLines().size())),
                                new Chunk<>(rChunk.getPosition() + minSize, Collections.emptyList())
                        )
                );
            }

            return deltas;
        }

        return List.of(delta);
    }

    private static void addEqualOrSimilarRows(List<String> original,
                                              List<String> revised,
                                              List<DiffRow> diffRows,
                                              int count,
                                              IndexPair indexPair) {
        int o = indexPair.o;
        int r = indexPair.r;

        for (int i = 0; i < count; i++) {
            var oLine = original.get(o);
            var rLine = revised.get(r);

            if (STRICT_EQUALIZER.equals(oLine, rLine)) {
                diffRows.add(DiffRow.newEqual(oLine));
            } else {
                diffRows.add(DiffRow.newSimilar(oLine, rLine));
            }

            o++;
            r++;
        }
    }

    private record IndexPair(int o, int r) {
    }

}
