package echoflux.core.word.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import echoflux.core.word.common.Word;

import java.util.List;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class WordPatcherTest {

    @Test
    void givenEmptyWords_whenPatchAllFromText_thenNewWords() {
        var revised = "This is";
        var result = WordPatcher.patchAllFromText(List.of(), revised, Word::new);

        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals("This", result.getFirst().getContent());
        Assertions.assertEquals(0, result.getFirst().getStartOffsetMillis());
        Assertions.assertEquals(0, result.getFirst().getEndOffsetMillis());
        Assertions.assertEquals("Unknown", result.getFirst().getSpeakerName());

        Assertions.assertEquals("is", result.getLast().getContent());
        Assertions.assertEquals(0, result.getLast().getStartOffsetMillis());
        Assertions.assertEquals(0, result.getLast().getEndOffsetMillis());
        Assertions.assertEquals("Unknown", result.getLast().getSpeakerName());
    }

    @Test
    void givenExactWords_whenPatchAllFromText_thenExactWords() {
        var original = List.of(
                new Word("This", "Speaker 1", 0L, 100L),
                new Word("is", "Speaker 1", 100L, 200L),
                new Word("a", "Speaker 1", 200L, 300L),
                new Word("test", "Speaker 1", 300L, 400L)
        );
        var revised = "This is a test";

        var result = WordPatcher.patchAllFromText(original, revised, Word::new);

        Assertions.assertEquals(original, result);
    }

    @Test
    void givenExactWords_whenPatchAllFromWords_thenExactWords() {
        var original = List.of(
                new Word("This", "Speaker 1", 0L, 100L),
                new Word("is", "Speaker 1", 100L, 200L),
                new Word("a", "Speaker 1", 200L, 300L),
                new Word("test", "Speaker 1", 300L, 400L)
        );
        var revised = List.of(
                new Word("This", "Speaker 1", -1L, -1L),
                new Word("is", "Speaker 1", -1L, -1L),
                new Word("a", "Speaker 1", -1L, -1L),
                new Word("test", "Speaker 1", -1L, -1L)
        );

        var result = WordPatcher.patchAllFromWords(original, revised, Word::new);

        Assertions.assertEquals(original, result);
    }

    @Test
    void givenSingleReplacedWord_whenPatchAll_thenSingleReplacedWord() {
        var words = List.of(
                new Word("This", "Speaker 1", 0L, 100L),
                new Word("is", "Speaker 1", 100L, 200L),
                new Word("a", "Speaker 1", 200L, 300L),
                new Word("test", "Speaker 1", 300L, 400L)
        );
        var raw = "ThiS is a pl fr";

        var result = WordPatcher.patchAllFromText(words, raw, Word::new);

        var expected = List.of(
                new Word("ThiS", "Speaker 1", 0L, 100L),
                new Word("is", "Speaker 1", 100L, 200L),
                new Word("a", "Speaker 1", 200L, 300L),
                new Word("pl", "Speaker 1", 300L, 350L),
                new Word("fr", "Speaker 1", 350L, 400L)
        );
        Assertions.assertEquals(expected, result);
    }

    @Test
    void givenSingleReplacedWord_whenPatchAllFromWords_thenSingleReplacedWord() {
        var words = List.of(
                new Word("This", "Speaker 1", 0L, 100L),
                new Word("is", "Speaker 1", 100L, 200L),
                new Word("a", "Speaker 1", 200L, 300L),
                new Word("test", "Speaker 1", 300L, 360L)
        );
        var revised = List.of(
                new Word("This", "Speaker 1", -1L, -1L),
                new Word("is", "Speaker 1", -1L, -1L),
                new Word("a", "Speaker 1", -1L, -1L),
                new Word("test", "Speaker 1", -1L, -1L),
                new Word("a", "Speaker 1", -1L, -1L),
                new Word("b", "Speaker 1", -1L, -1L)
        );

        var result = WordPatcher.patchAllFromWords(words, revised, Word::new);

        var expected = List.of(
                new Word("This", "Speaker 1", 0L, 100L),
                new Word("is", "Speaker 1", 100L, 200L),
                new Word("a", "Speaker 1", 200L, 300L),
                new Word("test", "Speaker 1", 300L, 340L),
                new Word("a", "Speaker 1", 340L, 350L),
                new Word("b", "Speaker 1", 350L, 360L)

        );
        Assertions.assertEquals(expected, result);
    }


}
