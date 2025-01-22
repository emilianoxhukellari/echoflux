import {html, css, LitElement, type TemplateResult} from 'lit';
import {customElement, property, state} from 'lit/decorators.js';
import {popoverRenderer} from '@vaadin/popover/lit.js';
import '@vaadin/icon';
import '@vaadin/icons';
import '@vaadin/popover';
import '@vaadin/vertical-layout';
import '@vaadin/scroller';

interface AudioTextConnectorBackend {
    onEditPart(sequence: number): void;
}

interface PartModel {
    text: string;
    sequence: number;
    startOffsetMillis: number;
    endOffsetMillis: number;
}

@customElement('audio-text-connector')
class AudioTextConnector extends LitElement {
    @property({type: String}) audioSrc = '';
    @property({type: Array}) partModels: PartModel[] = [];
    @state() private currentPartIndex = -1;

    static styles = css`
        :host {
            display: flex;
            flex-direction: column;
            height: 100%;
        }

        .audio {
            height: 40px;
            flex-shrink: 0;
            margin-top: 10px;
            width: 100%;
        }

        .text-container {
            padding: 10px;
            overflow-y: auto;
            flex-grow: 1;
        }

        .text-part {
            cursor: pointer;
            display: inline;
            white-space: pre-line;
            padding: 3px 0;
            position: relative;
        }

        .text-part:hover {
            background-color: var(--lumo-primary-color-10pct);
            border-radius: 6px;
        }

        .text-part.current {
            color: var(--lumo-warning-text-color);
        }
    `;

    declare $server: AudioTextConnectorBackend;
    private audioElement!: HTMLAudioElement;
    private textContainer!: HTMLElement;
    private partElements!: NodeListOf<HTMLSpanElement>;
    private readonly SCROLL_OFFSET = 80;

    render(): TemplateResult {
        return html`
            <div class="text-container">
                ${this.partModels.map((part: PartModel, index: number) =>
                        html`
                            <span id="part-${index}"
                                  class="text-part ${index === this.currentPartIndex ? 'current' : ''}"
                                  @click="${() => this.onClick(index)}">${part.text}</span>
                            <vaadin-popover
                                    theme="arrow"
                                    for="part-${index}"
                                    position="top"
                                    hide-delay="200"
                                    hover-delay="500"
                                    .trigger="${['hover', 'focus', 'click']}"
                                    ${popoverRenderer(() => this.popoverRenderer(part.sequence))}/>
                        `
                )}
            </div>
            <audio class="audio" controls src="${this.audioSrc}"></audio>
        `;
    }

    popoverRenderer(sequence: number) {
        return html`
            <vaadin-button @click="${() => this.$server.onEditPart(sequence)}">
                <vaadin-icon icon="lumo:edit"></vaadin-icon>
                Edit
            </vaadin-button>
        `;
    }

    firstUpdated(): void {
        this.audioElement = this.shadowRoot?.querySelector('audio') as HTMLAudioElement;
        this.textContainer = this.shadowRoot?.querySelector('.text-container') as HTMLElement;
        this.partElements = this.shadowRoot?.querySelectorAll('.text-part') as NodeListOf<HTMLSpanElement>;

        if (this.audioElement) {
            this.audioElement.addEventListener('timeupdate', this.onTimeUpdate);
        }
    }

    disconnectedCallback(): void {
        super.disconnectedCallback();
        if (this.audioElement) {
            this.audioElement.removeEventListener('timeupdate', this.onTimeUpdate);
        }
    }

    updatePart(part: PartModel) {
        const index = this.partModels.findIndex(p => p.sequence === part.sequence);
        if (index >= 0) {
            this.partModels[index] = part;
            this.requestUpdate('partModels');
        } else {
            throw new Error(`Part with sequence ${part.sequence} not found`);
        }
    }

    pause() {
        if (this.audioElement) {
            this.audioElement.pause();
        }
    }

    private onTimeUpdate = (): void => {
        if (!this.audioElement) {
            return;
        }

        const currentTimeMillis = this.audioElement.currentTime * 1000;
        const newPartIndex = this.findPartIndex(currentTimeMillis);

        if (newPartIndex !== this.currentPartIndex) {
            this.currentPartIndex = newPartIndex;
            this.scrollToCurrentPart();
        }
    };

    private onClick(index: number): void {
        const part = this.partModels[index];
        if (part && this.audioElement) {
            this.audioElement.currentTime = part.startOffsetMillis / 1000;
        }
    }

    private findPartIndex(currentTime: number): number {
        let low = 0;
        let high = this.partModels.length - 1;

        while (low <= high) {
            const mid = Math.floor((low + high) / 2);
            const {startOffsetMillis, endOffsetMillis} = this.partModels[mid];

            if (currentTime >= startOffsetMillis && currentTime < endOffsetMillis) {
                return mid;
            } else if (currentTime < startOffsetMillis) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return -1;
    }

    private scrollToCurrentPart(): void {
        if (!this.textContainer || !this.partElements || this.currentPartIndex < 0) {
            return;
        }

        const currentPart = this.partElements[this.currentPartIndex];
        if (currentPart) {
            const textContainerRect = this.textContainer.getBoundingClientRect();
            const currentPartRect = currentPart.getBoundingClientRect();

            if (currentPartRect.bottom > textContainerRect.bottom - this.SCROLL_OFFSET) {
                this.textContainer.scrollTo({
                    top: currentPart.offsetTop - this.textContainer.offsetTop - this.SCROLL_OFFSET,
                    behavior: 'smooth',
                });
            } else if (currentPartRect.top < textContainerRect.top + this.SCROLL_OFFSET) {
                this.textContainer.scrollTo({
                    top: currentPart.offsetTop - this.textContainer.offsetTop - this.SCROLL_OFFSET,
                    behavior: 'smooth',
                });
            }
        }
    }
}

export default AudioTextConnector;