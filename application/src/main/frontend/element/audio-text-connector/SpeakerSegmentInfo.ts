import {WordInfo} from "Frontend/element/audio-text-connector/WordInfo";
import {BaseSpeakerSegmentInfo} from "Frontend/element/audio-text-connector/BaseSpeakerSegmentInfo";

export interface SpeakerSegmentInfo extends BaseSpeakerSegmentInfo {
    startOffsetMillis: number;
    endOffsetMillis: number;
    words: WordInfo[];
}