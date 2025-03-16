import {css, html, LitElement, type TemplateResult} from 'lit';
import {customElement, property, state} from 'lit/decorators.js';
import '@vaadin/button';
import '@vaadin/icons';

@customElement('audio-segment-player')
class AudioSegmentPlayer extends LitElement {

    @property({type: String}) audioSrc = '';
    @property({type: Number}) startOffsetMillis = 0;
    @property({type: Number}) endOffsetMillis = 0;

    @state() private currentTimeMillis = 0;
    @state() private isPlaying = false;

    static styles = css`
        :host {
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 10px;
        }

        .container {
            display: flex;
            align-items: center;
            padding: 4px 8px 4px 4px;
            border-radius: 30px;
            background-color: #383a3c;
            height: 30px;
            width: 100%;
        }

        .controls {
            display: flex;
            gap: 4px;
            align-items: center;
            font-size: var(--lumo-font-size-s);
            height: 20px;
            width: 100%;
        }

        .progress-bar {
            flex-grow: 1;
            height: 4px;
            appearance: none;
            background: linear-gradient(to right, #f5f5f6 var(--progress), #5f6062 var(--progress));
            border-radius: 2px;
            outline: none;
            cursor: pointer;
        }

        .progress-bar::-webkit-slider-thumb {
            appearance: none;
            width: 10px;
            height: 10px;
            border-radius: 50%;
            background: #f5f5f6;
            visibility: hidden;
        }

        .progress-bar:hover::-webkit-slider-thumb {
            visibility: visible;
        }

        .play-pause-button {
            color: #ffffff;
            border-radius: 20px;
        }
    `;

    private audioElement!: HTMLAudioElement;

    render(): TemplateResult {
        const progress = (this.currentTimeMillis - this.startOffsetMillis) / (this.endOffsetMillis - this.startOffsetMillis) * 100;
        const fromTime = this.formatTime(this.currentTimeMillis);
        const toTime = this.formatTime(this.endOffsetMillis);
        return html`
            <div class="container">
                <div class="controls">
                    <vaadin-button
                            theme="small tertiary"
                            class="play-pause-button"
                            @click=${this.togglePlayPause}>
                        <vaadin-icon icon="vaadin:${this.isPlaying ? 'pause' : 'play'}"></vaadin-icon>
                    </vaadin-button>
                    <span>${fromTime} / ${toTime}</span>
                    <input
                            type="range"
                            class="progress-bar"
                            style="--progress: ${progress}%"
                            .value=${progress}
                            @input=${this.seek}
                            min="0"
                            max="100">
                </div>
                <audio src="${this.audioSrc}" @timeupdate=${this.onTimeUpdate}></audio>
            </div>
        `;
    }

    firstUpdated() {
        this.audioElement = this.shadowRoot?.querySelector('audio') as HTMLAudioElement;
        this.audioElement.addEventListener('loadedmetadata', () => {
            this.audioElement.currentTime = this.startOffsetMillis / 1000;
        });
    }

    private async togglePlayPause(): Promise<void> {
        if (this.isPlaying) {
            this.pause();
        } else {
            await this.play();
        }
    }

    private async play(): Promise<void> {
        if (this.audioElement.paused) {
            await this.audioElement.play();
            this.isPlaying = true;
        }
    }

    private pause(): void {
        this.audioElement.pause();
        this.isPlaying = false;
    }

    private async seek(event: Event): Promise<void> {
        const input = event.target as HTMLInputElement;
        const percentage = Number(input.value) / 100;
        const seekTime = this.startOffsetMillis + percentage * (this.endOffsetMillis - this.startOffsetMillis);
        this.audioElement.currentTime = seekTime / 1000;

        if (this.isPlaying) {
            await this.audioElement.play();
        }
    }

    private onTimeUpdate(): void {
        const currentTime = this.audioElement.currentTime * 1000;
        if (currentTime < this.startOffsetMillis) {
            this.audioElement.currentTime = this.startOffsetMillis / 1000;
        } else if (currentTime > this.endOffsetMillis) {
            this.audioElement.pause();
            this.isPlaying = false;
            this.audioElement.currentTime = this.startOffsetMillis / 1000;
        } else {
            this.currentTimeMillis = currentTime;
        }
    }

    private formatTime(milliseconds: number): string {
        const totalSeconds = Math.floor(milliseconds / 1000);
        const hours = Math.floor(totalSeconds / 3600);
        const minutes = Math.floor((totalSeconds % 3600) / 60);
        const seconds = totalSeconds % 60;
        if (hours > 0) {
            return `${hours}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
        }
        return `${minutes}:${seconds.toString().padStart(2, '0')}`;
    }
}

export default AudioSegmentPlayer;
