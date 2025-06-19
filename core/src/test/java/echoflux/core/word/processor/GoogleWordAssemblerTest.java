package echoflux.core.word.processor;

import echoflux.core.transcribe.google.GoogleWordAssembler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import echoflux.core.diarization.DiarizationEntry;

import java.util.List;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class GoogleWordAssemblerTest {

    @Test
    void givenSingleDiarizationEntryAndManyWords_whenAssembleAll_thenReturnSpeakerWordList() {
        var d1 = DiarizationEntry.builder()
                .startOffsetMillis(0)
                .endOffsetMillis(10)
                .speakerName("speaker")
                .build();

        var w1 = SpeechToTextWord.builder()
                .content("word1")
                .startOffsetMillis(0)
                .endOffsetMillis(10)
                .build();

        var w2 = SpeechToTextWord.builder()
                .content("word2")
                .startOffsetMillis(10)
                .endOffsetMillis(20)
                .build();

        var swList = GoogleWordAssembler.assembleAll(List.of(w1, w2), List.of(d1));

        Assertions.assertEquals(2, swList.size());
        Assertions.assertEquals("word1", swList.getFirst().getContent());
        Assertions.assertEquals("speaker", swList.getFirst().getSpeakerName());
        Assertions.assertEquals("word2", swList.get(1).getContent());
        Assertions.assertEquals("speaker", swList.get(1).getSpeakerName());
    }

    @ParameterizedTest
    @CsvSource({
            "word1, 0, 5, speaker1",
            "word2, 6, 9, speaker1",
            "word3, 10, 15, speaker2",
            "word4, 16, 20, speaker2",
            "word5, 21, 25, speaker3",
            "word6, 26, 30, speaker3",
            "word7, 31, 35, speaker4",
            "word8, 36, 40, speaker4"
    })
    void givenManyDiarizationEntriesAndSingleWord_whenAssembleAll_thenChooseBestSpeaker(String content,
                                                                                        int start,
                                                                                        int end,
                                                                                        String expectedSpeaker) {
        var word = new SpeechToTextWord(content, start, end);

        var diarizationEntries = List.of(
                new DiarizationEntry("speaker1", 0, 10),
                new DiarizationEntry("speaker2", 11, 20),
                new DiarizationEntry("speaker3", 22, 30),
                new DiarizationEntry("speaker4", 30, 40)
        );

        var result = GoogleWordAssembler.assembleAll(List.of(word), diarizationEntries);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(expectedSpeaker, result.getFirst().getSpeakerName());
    }

    @Test
    void givenWordBeforeAllDiarizationEntries_whenAssembleAll_chooseFirstDiarizationEntry() {
        var d1 = DiarizationEntry.builder()
                .startOffsetMillis(10)
                .endOffsetMillis(20)
                .speakerName("speaker1")
                .build();

        var d2 = DiarizationEntry.builder()
                .startOffsetMillis(20)
                .endOffsetMillis(30)
                .speakerName("speaker2")
                .build();

        var w1 = SpeechToTextWord.builder()
                .content("word1")
                .startOffsetMillis(0)
                .endOffsetMillis(5)
                .build();

        var swList = GoogleWordAssembler.assembleAll(List.of(w1), List.of(d1, d2));

        Assertions.assertEquals(1, swList.size());
        Assertions.assertEquals("word1", swList.getFirst().getContent());
        Assertions.assertEquals("speaker1", swList.getFirst().getSpeakerName());
    }

    @Test
    void givenManyWordsAndManyDiarizationSpeakers_whenAssembleAll_chooseBestSpeaker() {
        var d1 = new DiarizationEntry("speaker1", 0, 10);
        var d2 = new DiarizationEntry("speaker2", 11, 20);

        var w1 = new SpeechToTextWord("word1", 0, 5);
        var w2 = new SpeechToTextWord("word2", 6, 9);
        var w3 = new SpeechToTextWord("word3", 10, 15);
        var w4 = new SpeechToTextWord("word4", 16, 20);
        var w5 = new SpeechToTextWord("word5", 21, 25);

        var swList = GoogleWordAssembler.assembleAll(List.of(w1, w2, w3, w4, w5), List.of(d1, d2));

        Assertions.assertEquals(5, swList.size());
        Assertions.assertEquals("speaker1", swList.get(0).getSpeakerName());
        Assertions.assertEquals("word1", swList.get(0).getContent());

        Assertions.assertEquals("speaker1", swList.get(1).getSpeakerName());
        Assertions.assertEquals("word2", swList.get(1).getContent());

        Assertions.assertEquals("speaker2", swList.get(2).getSpeakerName());
        Assertions.assertEquals("word3", swList.get(2).getContent());

        Assertions.assertEquals("speaker2", swList.get(3).getSpeakerName());
        Assertions.assertEquals("word4", swList.get(3).getContent());

        Assertions.assertEquals("speaker2", swList.get(4).getSpeakerName());
        Assertions.assertEquals("word5", swList.get(4).getContent());
    }

    @ParameterizedTest(name = "{0} [{1}, {2}] -> {3}")
    @CsvSource({
            "run, 160, 1440, SPEAKER_00",
            "there's, 1440, 1920, SPEAKER_00",
            "nothing, 1920, 2400, SPEAKER_00",
            "like, 2400, 2760, SPEAKER_00",
            "watching, 2760, 3120, SPEAKER_00",
            "the, 3120, 3320, SPEAKER_00",
            "sunrise, 3320, 4040, SPEAKER_00",
            "while, 4040, 4280, SPEAKER_00",
            "drinking, 4280, 4960, SPEAKER_00",
            "my, 4960, 5080, SPEAKER_00",
            "morning, 5080, 5640, SPEAKER_00",
            "coffee., 5640, 7520, SPEAKER_00",
            "Really?, 7520, 9280, SPEAKER_01",
            "I, 9280, 9480, SPEAKER_01",
            "am, 9480, 9960, SPEAKER_01",
            "opposite., 9960, 11840, SPEAKER_01",
            "I, 11840, 11960, SPEAKER_01",
            "love, 11960, 12400, SPEAKER_01",
            "sleeping, 12400, 13000, SPEAKER_01",
            "in., 13000, 14160, SPEAKER_01"
    })
    void givenPyannoteDiarizationEntriesAndGoogleSpeechToTextWordsV1_whenAssembleAll_thenChooseBestSpeaker(String content,
                                                                                                           int start,
                                                                                                           int end,
                                                                                                           String expectedSpeaker) {
        var diarizationEntries = List.of(
                new DiarizationEntry("SPEAKER_00", 5, 505),
                new DiarizationEntry("SPEAKER_00", 1365, 6205),
                new DiarizationEntry("SPEAKER_01", 7345, 7945),
                new DiarizationEntry("SPEAKER_01", 9205, 10545),
                new DiarizationEntry("SPEAKER_01", 11425, 13185)
        );

        var word = new SpeechToTextWord(content, start, end);

        var swList = GoogleWordAssembler.assembleAll(List.of(word), diarizationEntries);

        Assertions.assertEquals(1, swList.size());
        Assertions.assertEquals(expectedSpeaker, swList.getFirst().getSpeakerName());
        Assertions.assertEquals(content, swList.getFirst().getContent());
    }


    @ParameterizedTest(name = "{0} [{1}, {2}] -> {3}")
    @CsvSource({
            "very, 5800, 6040, SPEAKER_00",
            "scary, 6040, 6400, SPEAKER_00",
            "one., 6400, 7440, SPEAKER_00",
            "this, 7440, 7640, SPEAKER_01",
            "is, 7640, 8040, SPEAKER_01",
            "a, 8080, 8240, SPEAKER_01",
    })
    void givenPyannoteDiarizationEntriesAndGoogleSpeechToTextWordsV2_whenAssembleAll_thenChooseBestSpeaker(String content,
                                                                                                           int start,
                                                                                                           int end,
                                                                                                           String expectedSpeaker) {
        var diarizationEntries = List.of(
                new DiarizationEntry("SPEAKER_00", 5, 6645),
                new DiarizationEntry("SPEAKER_01", 7325, 14985)
        );

        var word = new SpeechToTextWord(content, start, end);

        var swList = GoogleWordAssembler.assembleAll(List.of(word), diarizationEntries);

        Assertions.assertEquals(1, swList.size());
        Assertions.assertEquals(expectedSpeaker, swList.getFirst().getSpeakerName());
        Assertions.assertEquals(content, swList.getFirst().getContent());
    }

}
