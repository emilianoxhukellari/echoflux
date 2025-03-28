import {WordDto} from "Frontend/element/audio-text-connector/WordDto";

export interface SpeakerSegmentDto {
    startOffsetMillis: number;
    endOffsetMillis: number;
    speakerName: string;
    words: WordDto[];
    content: string;
}