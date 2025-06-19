import {SpeakerSegmentInfo} from "Frontend/element/audio-text-connector/SpeakerSegmentInfo";
import {WordInfo} from "Frontend/element/audio-text-connector/WordInfo";

export class WordUtils {

    public static buildSegmentsFromWords(words: WordInfo[]): SpeakerSegmentInfo[] {
        if (!words || words.length === 0) {
            return [];
        }

        const segments: SpeakerSegmentInfo[] = [];

        let currSegment: SpeakerSegmentInfo = {
            speakerName: words[0].speakerName,
            startOffsetMillis: words[0].startOffsetMillis,
            endOffsetMillis: words[0].endOffsetMillis,
            words: [words[0]],
            content: words[0].content
        };
        segments.push(currSegment);

        for (let i = 1; i < words.length; i++) {
            const w = words[i];
            if (w.speakerName === currSegment.speakerName) {
                currSegment.endOffsetMillis = w.endOffsetMillis;
                currSegment.words.push(w);
                currSegment.content += ` ${w.content}`;
            } else {
                currSegment = {
                    speakerName: w.speakerName,
                    startOffsetMillis: w.startOffsetMillis,
                    endOffsetMillis: w.endOffsetMillis,
                    words: [w],
                    content: w.content
                };
                segments.push(currSegment);
            }
        }

        return segments;
    }

}