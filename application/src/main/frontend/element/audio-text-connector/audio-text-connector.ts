import {html, css, LitElement, type TemplateResult} from 'lit';
import {customElement, property, state, queryAsync} from 'lit/decorators.js';
import '@vaadin/icon';
import '@vaadin/icons';
import '@vaadin/popover';
import '@vaadin/scroller';
import '@vaadin/vertical-layout';
import '@vaadin/horizontal-layout';
import '@vaadin/scroller';
import 'Frontend/element/audio-text-connector/speaker-segment';
import {SpeakerSegmentDto} from 'Frontend/element/audio-text-connector/SpeakerSegmentDto';
import {WordDto} from 'Frontend/element/audio-text-connector/WordDto';

interface AudioTextConnectorBackend {
    onEditPart(sequence: number): void;
}

@customElement('audio-text-connector')
export class AudioTextConnector extends LitElement {

    @property({type: String})
    audioSrc = '';

    @property({type: Array})
    speakerSegments: SpeakerSegmentDto[] = [];

    @property({type: Number})
    maxHighlightedWords = 15;

    @state()
    private startAtWordSequence = -1;

    @state()
    private currentTimeMillis = 0;

    @queryAsync('#audio')
    private audio!: Promise<HTMLAudioElement>;

    static readonly styles = css`
        :host {
            height: 100%;
        }

        .audio {
            height: 40px;
            flex-shrink: 0;
            width: 100%;
        }

        .speaker-segments {
            padding: 10px;
            display: flex;
            flex-direction: column;
            gap: 10px;
        }
        
        .main-vl {
            height: 100%;
            width: 100%;
        }
    `;

    declare $server: AudioTextConnectorBackend;

    protected render(): TemplateResult {
        return html`
            <vaadin-vertical-layout class="main-vl" theme="spacing">
                <vaadin-scroller>
                    ${this.renderSegments()}
                </vaadin-scroller>
                <audio id="audio" class="audio" src="${this.audioSrc}" controls></audio>
            </vaadin-vertical-layout>
        `;
    }

    private renderSegments(): TemplateResult {
        return html`
            <div class="speaker-segments">
                ${
                        this.speakerSegments.map(segment => html`
                            <speaker-segment .segment="${segment}"
                                             .currentTimeMillis="${this.currentTimeMillis}"
                                             .startAtWordSequence="${this.startAtWordSequence}"
                                             .maxHighlightedWords="${this.maxHighlightedWords}"
                                             @word-click="${(e: CustomEvent) => this.onWordClick((e.detail as WordDto))}"
                                             @edit-word="${this.onEditWord}"></speaker-segment>`
                        )
                }
            </div>
        `;
    }

    async connectedCallback(): Promise<void> {
        super.connectedCallback();

        const audio = await this.audio;
        audio.addEventListener('timeupdate', this.onTimeUpdate);
    }

    async disconnectedCallback(): Promise<void> {
        super.disconnectedCallback();

        const audio = await this.audio;
        audio.removeEventListener('timeupdate', this.onTimeUpdate);
    }

    private onTimeUpdate = async (): Promise<void> => {
        const audio = await this.audio;
        this.currentTimeMillis = audio.currentTime * 1000;
    }

    private async onWordClick(word: WordDto) {
        const audio = await this.audio;
        audio.currentTime = word.startOffsetMillis / 1000;

        this.startAtWordSequence = word.sequence;
    }

    private onEditWord(event: CustomEvent): void {
        this.$server.onEditPart(event.detail);
    }

}