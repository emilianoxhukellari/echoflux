import {BaseSpeakerSegmentInfo} from "Frontend/element/audio-text-connector/BaseSpeakerSegmentInfo";
import {WordInfo} from "Frontend/element/audio-text-connector/WordInfo";

export interface AudioTextConnectorBackend {

    saveAllSpeakerSegments(speakerSegments: BaseSpeakerSegmentInfo[]): Promise<WordInfo[]>;

    downloadTranscript(): Promise<void>;

}