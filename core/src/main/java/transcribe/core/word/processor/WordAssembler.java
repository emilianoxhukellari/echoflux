package transcribe.core.word.processor;

import org.apache.commons.lang3.Validate;
import transcribe.core.core.utils.MoreLists;
import transcribe.core.diarization.DiarizationEntry;
import transcribe.core.word.common.SpeechToTextWord;
import transcribe.core.word.common.WordInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class WordAssembler {

    public static <T extends WordInfo> List<T> assembleAll(List<SpeechToTextWord> words,
                                                           List<DiarizationEntry> diarizationEntries,
                                                           Supplier<T> newWord) {
        Validate.notEmpty(words, "words");
        Validate.notEmpty(diarizationEntries, "diarizationEntries");
        Objects.requireNonNull(newWord, "newWord");

        var wList = MoreLists.toSorted(words, SpeechToTextWord::getStartOffsetMillis);
        var dList = MoreLists.toSorted(diarizationEntries, DiarizationEntry::getStartOffsetMillis);
        var swList = new ArrayList<T>(wList.size());

        var dItr = dList.iterator();
        DiarizationEntry currD = dItr.next();
        DiarizationEntry prevD = null;

        for (var w : wList) {
            long wMid = (w.getStartOffsetMillis() + w.getEndOffsetMillis()) / 2;

            while (wMid >= currD.getEndOffsetMillis() && dItr.hasNext()) {
                prevD = currD;
                currD = dItr.next();
            }

            var de = getDiarizationEntry(currD, prevD, wMid);
            var sw = toWord(w, de, newWord);
            swList.add(sw);
        }

        return Collections.unmodifiableList(swList);
    }

    private static DiarizationEntry getDiarizationEntry(DiarizationEntry currD, DiarizationEntry prevD, long wMid) {
        if (wMid >= currD.getStartOffsetMillis() && wMid < currD.getEndOffsetMillis()) {
            return currD;
        }

        if (prevD == null) {
            return currD;
        }

        long prevEnd = prevD.getEndOffsetMillis();
        long currStart = currD.getStartOffsetMillis();

        long diffPrev = Math.abs(wMid - prevEnd);
        long diffCurr = Math.abs(wMid - currStart);

        return diffPrev < diffCurr ? prevD : currD;
    }

    private static <T extends WordInfo> T toWord(SpeechToTextWord speechToTextWord,
                                                    DiarizationEntry diarization,
                                                    Supplier<T> newWord) {
        Objects.requireNonNull(speechToTextWord);
        Objects.requireNonNull(diarization);

        var word = newWord.get();
        word.setContent(speechToTextWord.getContent());
        word.setSpeakerName(diarization.getSpeakerName());
        word.setStartOffsetMillis(speechToTextWord.getStartOffsetMillis());
        word.setEndOffsetMillis(speechToTextWord.getEndOffsetMillis());

        return word;
    }

}
