package transcribe.core.word.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import transcribe.core.word.common.Word;

import java.util.List;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class WordPatcherTest {

    @Test
    void givenEmptyWords_whenPatchAll_thenNewWords() {
        var revised = "This is";
        var result = WordPatcher.patchAll(List.of(), revised, Word::new);

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
    void givenExactWords_whenPatchAll_thenExactWords() {
        var original = List.of(
                new Word("This", "Speaker 1", 0L, 100L),
                new Word("is", "Speaker 1", 100L, 200L),
                new Word("a", "Speaker 1", 200L, 300L),
                new Word("test", "Speaker 1", 300L, 400L)
        );
        var revised = "This is a test";

        var result = WordPatcher.patchAll(original, revised, Word::new);

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

        var result = WordPatcher.patchAll(words, raw, Word::new);

        var expected = List.of(
                new Word("ThiS", "Speaker 1", 0L, 100L),
                new Word("is", "Speaker 1", 100L, 200L),
                new Word("a", "Speaker 1", 200L, 300L),
                new Word("pl", "Speaker 1", 300L, 350L),
                new Word("fr", "Speaker 1", 350L, 400L)
        );
        Assertions.assertEquals(expected, result);
    }

}
