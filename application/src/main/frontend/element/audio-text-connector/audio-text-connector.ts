import {html, css, LitElement, type TemplateResult} from 'lit';
import {customElement, property, state, queryAsync} from 'lit/decorators.js';
import '@material/web/labs/segmentedbuttonset/outlined-segmented-button-set.js';
import '@material/web/labs/segmentedbutton/outlined-segmented-button.js';
import '@vaadin/icon';
import '@vaadin/icons';
import '@vaadin/popover';
import '@vaadin/scroller';
import '@vaadin/vertical-layout';
import '@vaadin/horizontal-layout';
import '@vaadin/scroller';
import {dialogRenderer} from "@vaadin/dialog/lit";
import 'Frontend/element/audio-text-connector/speaker-segment.ts';
import 'Frontend/element/la-ball-clip-rotate-pulse.ts';
import 'Frontend/element/ts-audio/ts-audio.ts';
import {SpeakerSegmentDto} from 'Frontend/element/audio-text-connector/SpeakerSegmentDto';
import {WordDto} from 'Frontend/element/audio-text-connector/WordDto';
import {AudioTextConnectorBackend} from "Frontend/element/audio-text-connector/AudioTextConnectorBackend";
import {SeekSequenceAndTime} from "Frontend/element/audio-text-connector/SeekSequenceAndTime";

@customElement('audio-text-connector')
export class AudioTextConnector extends LitElement {

    @property({type: String})
    audioSrc = '';

    @property({type: Array})
    speakerSegments: SpeakerSegmentDto[] = [];

    @property({type: Number})
    maxHighlightedWords = 15;

    @state()
    private seekPosition: SeekSequenceAndTime = {timeMillis: 0, sequence: -1};

    @state()
    private timeMillis = 0;

    @state()
    private edit = false;

    @state()
    private saving = false;

    static readonly styles = css`
        :host {
            height: 100%;
            width: 100%;
            --md-outlined-segmented-button-container-height: 36px;
            --md-outlined-segmented-button-icon-size: 15px;
        }

        .panel-layout {
            width: 100%;
            justify-content: space-between;
            align-items: center;
            background-color: var(--lumo-base-color);
            padding: 8px 14px;
            border-radius: 10px;
            border: 1px solid var(--lumo-contrast-10pct);
        }

        .scroller {
            width: 100%;
            height: 100%;
            background-color: var(--lumo-base-color);
            border-radius: 10px;
            border: 1px solid var(--lumo-contrast-10pct);
        }

        .main {
            height: 100%;
            width: 100%;
            background-color: var(--lumo-shade-5pct);
            border-radius: 15px;
            align-items: center;
            padding: 30px;
        }

        .button {
            cursor: pointer;
            padding: 0;
        }

    `;

    declare $server: AudioTextConnectorBackend;

    protected render(): TemplateResult {
        return html`
            ${this.renderSavingDialog()}
            <vaadin-vertical-layout class="main" theme="spacing-s">
                ${this.renderPanel()}
                <vaadin-scroller class="scroller"
                                 scroll-direction="vertical">
                    ${this.renderSegments()}
                </vaadin-scroller>
                <ts-audio id="tsAudio"
                          .src="${this.audioSrc}"
                          .seekPosition="${this.seekPosition}"
                          @time-update="${(e: CustomEvent) => this.timeMillis = e.detail}"/>
            </vaadin-vertical-layout>
        `;
    }

    private renderSegments(): TemplateResult {
        return html`
            <vaadin-vertical-layout theme="spacing-s padding">
                ${
                        this.speakerSegments.map((s, i) => html`
                            <ts-speaker-segment .segment="${s}"
                                                .timeMillis="${this.timeMillis}"
                                                .seekPosition="${this.seekPosition}"
                                                .maxHighlightedWords="${this.maxHighlightedWords}"
                                                .edit="${this.edit}"
                                                @segment-change="${(e: CustomEvent) => this.handleSegmentChange((e.detail as SpeakerSegmentDto), i)}"
                                                @word-click="${(e: CustomEvent) => this.handleWordClick((e.detail as WordDto))}">`
                        )
                }
            </vaadin-vertical-layout>
        `;
    }

    private renderSavingDialog(): TemplateResult {
        return html`
            <vaadin-dialog .opened="${this.saving}"
                           no-close-on-outside-click
                           no-close-on-esc
                           ${dialogRenderer(
                                   () => html`
                                       <vaadin-vertical-layout theme="spacing padding"
                                                               style="align-items: center; width: 400px">
                                           <h3>Saving...</h3>
                                           <la-ball-clip-rotate-pulse/>
                                       </vaadin-vertical-layout>
                                   `
                           )}
            />
        `;
    }

    private renderPanel(): TemplateResult {
        return html`
            <vaadin-horizontal-layout theme="spacing" class="panel-layout">
                <vaadin-horizontal-layout theme="spacing" style="align-items: center">
                    <md-outlined-segmented-button-set style="width: 155px">
                        <md-outlined-segmented-button .selected="${!this.edit}"
                                                      .label="${this.edit ? 'Save' : 'View'}"
                                                      @click="${() => this.handleSaveClick()}">
                        </md-outlined-segmented-button>
                        <md-outlined-segmented-button .selected="${this.edit}"
                                                      label="Edit"
                                                      @click="${() => this.edit = true}">
                        </md-outlined-segmented-button>
                    </md-outlined-segmented-button-set>
                </vaadin-horizontal-layout>
                <vaadin-horizontal-layout theme="spacing" style="align-items: center">
                    <vaadin-button theme="icon"
                                   class="button"
                                   @click="${() => this.$server.downloadTranscript()}">
                        <vaadin-icon icon="vaadin:download"/>
                    </vaadin-button>
                </vaadin-horizontal-layout>
            </vaadin-horizontal-layout>
        `;
    }

    private async handleSaveClick() {
        if (!this.edit) {
            return;
        }

        this.saving = true;
        this.speakerSegments = await this.$server.saveAllSpeakerSegments(this.speakerSegments);
        this.saving = false;
        this.edit = false;
    }

    private handleSegmentChange(segment: SpeakerSegmentDto, index: number): void {
        this.speakerSegments[index] = segment;
    }

    private handleWordClick(word: WordDto): void {
        this.seekPosition = {timeMillis: word.startOffsetMillis, sequence: word.sequence};
    }

}