import {html, css, LitElement, type TemplateResult, PropertyValues} from 'lit';
import {customElement, property, state} from 'lit/decorators.js';
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
import 'Frontend/element/progress/la-ball-clip-rotate-pulse.ts';
import 'Frontend/element/ef-audio/ef-audio.ts';
import {SpeakerSegmentInfo} from 'Frontend/element/audio-text-connector/SpeakerSegmentInfo';
import {WordInfo} from 'Frontend/element/audio-text-connector/WordInfo';
import {AudioTextConnectorBackend} from "Frontend/element/audio-text-connector/AudioTextConnectorBackend";
import {SeekSequenceAndTime} from "Frontend/element/audio-text-connector/SeekSequenceAndTime";
import {BaseSpeakerSegmentInfo} from "Frontend/element/audio-text-connector/BaseSpeakerSegmentInfo";
import {WordUtils} from "Frontend/element/audio-text-connector/WordUtils";

@customElement('audio-text-connector')
export class AudioTextConnector extends LitElement {

    @property({type: String})
    audioSrc = '';

    @property({type: Array})
    words: WordInfo[] = [];

    @property({type: Number})
    maxHighlightedWords = 15;

    @state()
    speakerSegments: SpeakerSegmentInfo[] = [];

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
            border-radius: var(--lumo-border-radius-xl);
            border: 1px solid var(--lumo-contrast-10pct);
        }

        .scroller {
            width: 100%;
            height: 100%;
            background-color: var(--lumo-base-color);
            border-radius: var(--lumo-border-radius-xl);
            border: 1px solid var(--lumo-contrast-10pct);
        }

        .main {
            height: 100%;
            width: 100%;
            background-color: var(--lumo-shade-5pct);
            border-radius: var(--lumo-border-radius-xl);
            align-items: center;
            padding: var(--lumo-space-l);
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
                <ef-audio .src="${this.audioSrc}"
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
                            <speaker-segment .segment="${s}"
                                             .timeMillis="${this.timeMillis}"
                                             .seekPosition="${this.seekPosition}"
                                             .maxHighlightedWords="${this.maxHighlightedWords}"
                                             .edit="${this.edit}"
                                             @segment-change="${(e: CustomEvent) => this.handleSegmentChange((e.detail as SpeakerSegmentInfo), i)}"
                                             @word-click="${(e: CustomEvent) => this.handleWordClick((e.detail as WordInfo))}">`
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

    protected willUpdate(_changedProperties: PropertyValues) {
        if(_changedProperties.has('words')) {
            this.speakerSegments = WordUtils.buildSegmentsFromWords(this.words);
        }
    }

    private async handleSaveClick() {
        if (!this.edit) {
            return;
        }

        this.saving = true;
        const baseSpeakerSegments = this.speakerSegments.map(
            s => ({speakerName: s.speakerName, content: s.content}) as BaseSpeakerSegmentInfo
        );
        this.words = await this.$server.saveAllSpeakerSegments(baseSpeakerSegments);
        this.saving = false;
        this.edit = false;
    }

    private handleSegmentChange(segment: SpeakerSegmentInfo, index: number): void {
        this.speakerSegments[index] = segment;
    }

    private handleWordClick(word: WordInfo): void {
        this.seekPosition = {timeMillis: word.startOffsetMillis, sequence: word.sequence};
    }

}