import {css, html, LitElement, PropertyValues, TemplateResult} from "lit";
import {styleMap} from 'lit/directives/style-map.js';
import {Duration} from 'luxon';
import {customElement, property, queryAsync, state} from "lit/decorators.js";
import '@material/web/slider/slider.js';
import {popoverRenderer} from "@vaadin/popover/lit";
import {SeekTime} from "Frontend/element/audio-text-connector/SeekTime";

@customElement('ts-audio')
export class TsAudio extends LitElement {

    @property({type: String})
    src = '';

    @property({type: Object})
    seekPosition: SeekTime = {timeMillis: 0};

    @state()
    private timeMillis = 0;

    @state()
    private volume = 1;

    @state()
    private durationMillis = 0;

    @state()
    private playing = false;

    @queryAsync('#internalAudio')
    private internalAudio!: Promise<HTMLAudioElement>;

    static readonly styles = css`

        :host {
            width: 100%;
        }

        vaadin-popover {
            --vaadin-popover-offset-bottom: 15px;
        }

        .button {
            cursor: pointer;
            padding: 0;
        }

        .slider {
            flex-grow: 1;
            --md-slider-handle-width: 16px;
            --md-slider-handle-height: 16px;
            --md-slider-active-track-height: 8px;
            --md-slider-inactive-track-height: 6px;
            --md-slider-active-track-shape: 2px;
            --md-slider-inactive-track-shape: 2px;
            --md-slider-state-layer-size: 14px;
            min-inline-size: 50px;
        }

        .main {
            width: 100%;
            align-items: center;
            background: var(--lumo-base-color);
            padding: 4px 10px;
            border-radius: 10px;
            border: 1px solid var(--lumo-contrast-10pct);
        }

        .time-label {
            white-space: nowrap;
            font-size: var(--lumo-font-size-s);
        }

    `;

    protected async willUpdate(_changedProperties: PropertyValues) {
        if (_changedProperties.has('seekPosition')) {
            await this.setInternalAudioTimeMillis(this.seekPosition.timeMillis);
        }
    }

    protected render(): TemplateResult {
        return html`
            <vaadin-horizontal-layout class="main" theme="spacing-s" part="main-layout">
                <vaadin-button theme="icon small"
                               class="button"
                               part="play-pause-button"
                               @click="${this.togglePlay}">
                    <vaadin-icon icon="${this.playing ? 'vaadin:pause' : 'vaadin:play'}"/>
                </vaadin-button>
                <label class="time-label" part="time-label">${this.formattedTime}</label>
                <md-slider step="0.1"
                           min="0"
                           max="${this.durationMillis}"
                           .value="${this.timeMillis}"
                           @input="${this.onTimeInput}"
                           part="slider"
                           class="slider"></md-slider>
                <vaadin-button theme="icon small"
                               part="volume-button"
                               class="button">
                    <vaadin-icon id="volume" icon="${this.volumeIcon}"/>
                    <vaadin-popover for="volume"
                                    part="volume-popover"
                                    theme="arrow"
                                    .trigger="${['hover', 'focus', 'click']}"
                                    ${popoverRenderer(this.renderVolumePopover)}
                                    position="top"/>
                </vaadin-button>
            </vaadin-horizontal-layout>
            ${this.renderInternalAudio()}
        `;
    }

    private async setInternalAudioTimeMillis(timeMillis: number): Promise<void> {
        const audio = await this.internalAudio;
        audio.currentTime = timeMillis / 1000;
    }

    private renderVolumePopover(): TemplateResult {
        const styles = {
            '--md-slider-handle-width': '12px',
            '--md-slider-handle-height': '12px',
            '--md-slider-active-track-height': '6px',
            '--md-slider-inactive-track-height': '4px',
            '--md-slider-active-track-shape': '2px',
            '--md-slider-inactive-track-shape': '2px',
            '--md-slider-state-layer-size': '14px',
            'width': '150px',
            'min-inline-size': '50px'
        };

        return html`
            <md-slider step="0.05"
                       min="0"
                       max="1"
                       style="${styleMap(styles)}"
                       @input="${this.handleVolumeInput}">
            </md-slider>
        `;
    }

    private renderInternalAudio(): TemplateResult {
        return html`
            <audio id="internalAudio"
                   style="display: none"
                   src="${this.src}"
                   @timeupdate="${this.handleTimeUpdate}"
                   @volumechange="${this.handleVolumeChange}"
                   @loadedmetadata="${this.handleLoadedMetadata}"
                   @play="${() => this.playing = true}"
                   @pause="${() => this.playing = false}"/>
        `;
    }

    private get volumeIcon(): string {
        if (this.volume === 0) {
            return 'vaadin:volume-off';
        }
        if (this.volume <= 0.3) {
            return 'vaadin:volume-down';
        }
        if (this.volume <= 0.7) {
            return 'vaadin:volume';
        }
        return 'vaadin:volume-up';
    }

    private get formattedTime(): string {
        const start = this.formatTime(this.timeMillis);
        const end = this.formatTime(this.durationMillis);

        return `${start} / ${end}`;
    }

    private handleTimeUpdate = async (): Promise<void> => {
        const audio = await this.internalAudio;
        this.timeMillis = audio.currentTime * 1000;

        this.dispatchEvent(new CustomEvent('time-update', {detail: this.timeMillis, bubbles: true}));
    }

    private handleVolumeChange = async (): Promise<void> => {
        const audio = await this.internalAudio;
        this.volume = audio.volume;
    }

    private handleLoadedMetadata = async (): Promise<void> => {
        const audio = await this.internalAudio;
        this.durationMillis = audio.duration * 1000;
    }

    private onTimeInput = async (event: Event): Promise<void> => {
        const input = event.target as HTMLInputElement;
        const currentTimeMillis = parseFloat(input.value);
        await this.setInternalAudioTimeMillis(currentTimeMillis);
    }

    private handleVolumeInput = async (event: Event): Promise<void> => {
        const input = event.target as HTMLInputElement;
        const volume = parseFloat(input.value);
        const audio = await this.internalAudio;
        audio.volume = volume;
    }

    private async togglePlay(): Promise<void> {
        const internalAudio = await this.internalAudio;

        if (internalAudio.paused) {
            await internalAudio.play();
        } else {
            internalAudio.pause();
        }
    }

    private formatTime(millis: number): string {
        const duration = Duration.fromMillis(millis).shiftTo('hours', 'minutes', 'seconds');

        return duration.hours > 0
            ? duration.toFormat('hh:mm:ss')
            : duration.toFormat('mm:ss');
    }

}