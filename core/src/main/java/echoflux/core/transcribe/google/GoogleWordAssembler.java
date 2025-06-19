package echoflux.core.transcribe.google;

import com.google.cloud.speech.v2.WordInfo;
import echoflux.core.core.utils.MoreLists;
import echoflux.core.diarization.DiarizationEntry;
import echoflux.core.word.common.Word;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class GoogleWordAssembler {

    private static final double EPS = 1e-6;
    private static final double STAY_NO_PUNCTUATION_PROB = 0.999;
    private static final double STAY_PUNCTUATION_PROB = 0.001;

    public static List<Word> assembleAll(List<WordInfo> words,
                                         List<DiarizationEntry> diarizationEntries) {
        Objects.requireNonNull(words, "words");
        Objects.requireNonNull(diarizationEntries, "diarizationEntries");

        var sortedWords = words.stream()
                .map(w -> Word.builder()
                        .content(w.getWord())
                        .startOffsetMillis(protobufDurationToMillis(w.getStartOffset()))
                        .endOffsetMillis(protobufDurationToMillis(w.getEndOffset()))
                        .build())
                .sorted(Comparator.comparing(Word::getStartOffsetMillis))
                .toList();

        var sortedDiarizationEntries = MoreLists.toSorted(diarizationEntries, DiarizationEntry::getStartOffsetMillis);

        var speakers = sortedDiarizationEntries.stream()
                .map(DiarizationEntry::getSpeakerName)
                .distinct()
                .toList();

        int speakersSize = speakers.size();
        int wordsSize = sortedWords.size();

        var speakerToDiarizationEntries = sortedDiarizationEntries.stream()
                .collect(Collectors.groupingBy(DiarizationEntry::getSpeakerName));

        double[][] emission = new double[wordsSize][speakersSize];

        for (int t = 0; t < wordsSize; t++) {
            var word = sortedWords.get(t);
            double wordDuration = word.getEndOffsetMillis() - word.getStartOffsetMillis();

            for (int s = 0; s < speakersSize; s++) {
                double overlap = 0;

                for (var diarizationEntry : speakerToDiarizationEntries.get(speakers.get(s))) {
                    long start = Math.max(word.getStartOffsetMillis(), diarizationEntry.getStartOffsetMillis());
                    long end = Math.min(word.getEndOffsetMillis(), diarizationEntry.getEndOffsetMillis());
                    if (end > start) {
                        overlap += (end - start);
                    }
                }

                double ratio = wordDuration > 0 ? overlap / wordDuration : 0;
                emission[t][s] = Math.max(ratio, EPS);
            }
        }

        boolean[] hasPunctuation = new boolean[wordsSize];
        for (int t = 0; t < wordsSize; t++) {
            var content = sortedWords.get(t).getContent();
            hasPunctuation[t] = hasPunctuation(content);
        }

        double[][] dp = new double[wordsSize][speakersSize];
        int[][] back = new int[wordsSize][speakersSize];

        for (int s = 0; s < speakersSize; s++) {
            dp[0][s] = Math.log(emission[0][s]);
            back[0][s] = -1;
        }

        for (int t = 1; t < wordsSize; t++) {
            boolean previousHasPunctuation = hasPunctuation[t - 1];

            double stayProb = previousHasPunctuation ? STAY_PUNCTUATION_PROB : STAY_NO_PUNCTUATION_PROB;
            double changeProb = 1.0 - stayProb;
            double stayLogProb = Math.log(stayProb);
            double changeLogProb = Math.log(changeProb) - Math.log(speakersSize - 1);

            for (int s = 0; s < speakersSize; s++) {
                double bestLogProb = Double.NEGATIVE_INFINITY;
                int bestPreviousSpeakerIdx = -1;

                for (int sp = 0; sp < speakersSize; sp++) {
                    double transitionLogProb = sp == s ? stayLogProb : changeLogProb;
                    double candidateLogProb = dp[t - 1][sp] + transitionLogProb;

                    if (candidateLogProb > bestLogProb) {
                        bestLogProb = candidateLogProb;
                        bestPreviousSpeakerIdx = sp;
                    }
                }

                dp[t][s] = bestLogProb + Math.log(emission[t][s]);
                back[t][s] = bestPreviousSpeakerIdx;
            }
        }

        double bestLastLogProb = Double.NEGATIVE_INFINITY;
        int bestLastSpeakerIdx = -1;
        for (int s = 0; s < speakersSize; s++) {
            double logProb = dp[wordsSize - 1][s];
            if (logProb > bestLastLogProb) {
                bestLastLogProb = logProb;
                bestLastSpeakerIdx = s;
            }
        }

        int[] speakerSequence = new int[wordsSize];
        int currentSpeaker = bestLastSpeakerIdx;
        for (int t = wordsSize - 1; t >= 0; t--) {
            speakerSequence[t] = currentSpeaker;
            currentSpeaker = back[t][currentSpeaker];
        }

        for (int t = 0; t < wordsSize; t++) {
            var speakerName = speakers.get(speakerSequence[t]);
            var word = sortedWords.get(t);
            word.setSpeakerName(speakerName);
        }

        return sortedWords;
    }

    private static boolean hasPunctuation(String str) {
        char last = str.charAt(str.length() - 1);

        return last == ',' || last == '.' || last == '?' || last == '!' || last == ';' || last == ':';
    }

    private static long protobufDurationToMillis(com.google.protobuf.Duration duration) {
        return duration.getSeconds() * 1_000L + duration.getNanos() / 1_000_000L;
    }

}
