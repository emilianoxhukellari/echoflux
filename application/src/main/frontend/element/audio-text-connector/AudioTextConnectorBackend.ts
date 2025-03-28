import {SpeakerSegmentDto} from "Frontend/element/audio-text-connector/SpeakerSegmentDto";

export interface AudioTextConnectorBackend {

    saveAllSpeakerSegments(speakerSegments: SpeakerSegmentDto[]): Promise<SpeakerSegmentDto[]>;

    downloadTranscript(): Promise<void>;

}