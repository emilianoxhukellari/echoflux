import {css, html, LitElement, PropertyValues, TemplateResult} from 'lit';
import {customElement, property, state} from 'lit/decorators.js';
import {classMap} from 'lit/directives/class-map.js';
import '@vaadin/icon';
import '@vaadin/icons';
import '@vaadin/popover';
import {SpeakerSegmentDto} from "Frontend/element/audio-text-connector/SpeakerSegmentDto";
import {WordDto} from "Frontend/element/audio-text-connector/WordDto";
import {CollectionUtils} from "Frontend/utils/CollectionUtils";
import {SeekSequence} from "Frontend/element/audio-text-connector/SeekSequence";

@customElement('speaker-segment')
export class SpeakerSegment extends LitElement {

    @property({type: Object})
    segment!: SpeakerSegmentDto;

    @property({type: Number})
    timeMillis = 0;

    @property({type: Object})
    seekPosition: SeekSequence = {sequence: -1};

    @property({type: Number})
    maxHighlightedWords = 15;

    @property({type: Boolean})
    edit = false;

    @state({
        hasChanged(value: HighlightedRange, oldValue: HighlightedRange): boolean {
            return !oldValue
                || value.startIndexInclusive !== oldValue.startIndexInclusive
                || value.endIndexExclusive !== oldValue.endIndexExclusive;
        }
    })
    private highlightedRange: HighlightedRange = {startIndexInclusive: -1, endIndexExclusive: 0};

    static readonly styles = css`
        :host {
            height: 100%;
            width: 100%;
        }

        .word-list-container {
            width: 100%;
            padding: 10px;
        }

        .word-list {
            display: flex;
            flex-wrap: wrap;
            gap: 3px;
        }

        .word {
            cursor: pointer;
            display: inline;
            position: relative;
            white-space: pre-line;
        }

        .word.highlighted {
            color: var(--lumo-warning-text-color);
        }

        .word:hover {
            background-color: var(--lumo-primary-color-10pct);
            border-radius: 6px;
        }

        .speaker {
            margin-right: 10px;
            text-align: right;
            padding: 3px 0;
        }

        .textarea {
            width: 100%;
            background-color: var(--lumo-shade-10pct);
            padding: 8px 10px 9px;
            border-radius: 10px;
            font: inherit;
            resize: none;
            border: none;
            color: inherit;
            field-sizing: content;
            word-spacing: -0.98px;
            line-height: 29px;
        }

        @supports not (field-sizing: content) {
            .textarea {
                resize: vertical;
                height: 150px;
            }
        }

        .textarea:focus {
            outline: none;
        }

    `;

    protected render() {
        return this.edit ? this.renderEditMode() : this.renderReadMode();
    }

    private renderEditMode(): TemplateResult {
        return html`
            <vaadin-vertical-layout theme="padding">
                <div class="speaker">${this.segment.speakerName}</div>
                <textarea class="textarea" @input="${this.onContentChange}"
                >${this.segment.content}</textarea>
            </vaadin-vertical-layout>
        `;
    }

    private renderReadMode(): TemplateResult {
        return html`
            <vaadin-vertical-layout theme="padding">
                <div class="speaker">${this.segment.speakerName}</div>
                <div class="word-list-container">
                    <div class="word-list">
                        ${this.segment.words.map((word: WordDto, index: number) => {
                            const wordClassInfo = {
                                highlighted: index >= this.highlightedRange.startIndexInclusive
                                        && index < this.highlightedRange.endIndexExclusive,
                                word: true
                            };
                            return html`
                                <span id="word-${word.sequence}"
                                      class="${classMap(wordClassInfo)}"
                                      @click="${() => this.onWordClick(word)}">${word.content}</span>
                            `;
                        })}
                    </div>
                </div>

            </vaadin-vertical-layout>
        `;
    }

    private onWordClick(word: WordDto) {
        this.dispatchEvent(new CustomEvent('word-click', {detail: word, bubbles: true}));
    }

    private onContentChange(event: Event) {
        const textarea = event.target as HTMLTextAreaElement;
        this.segment.content = textarea.value;
        this.onSegmentChange();
    }

    private onSegmentChange() {
        this.dispatchEvent(new CustomEvent('segment-change', {detail: this.segment, bubbles: true}));
    }

    protected willUpdate(_changedProperties: PropertyValues) {
        if (_changedProperties.has('seekPosition')
            && this.seekPosition.sequence >= this.segment.words[0].sequence
            && this.seekPosition.sequence <= this.segment.words[this.segment.words.length - 1].sequence) {

            const newStartIndex = CollectionUtils.binarySearch(
                this.segment.words,
                SpeakerSegment.newSequenceOnlyWordModel(this.seekPosition.sequence),
                (a, b) => a.sequence - b.sequence
            );

            if (newStartIndex >= 0) {
                this.highlightedRange = this.newHighlightedRangeFromStartIndex(newStartIndex);
            }
        }

        if (_changedProperties.has('timeMillis')) {
            this.highlightedRange = this.getHighlightedRange();
        }
    }

    private getHighlightedRange(): HighlightedRange {
        if (this.timeMillis >= this.segment.endOffsetMillis
            || this.timeMillis < this.segment.startOffsetMillis) {

            return {startIndexInclusive: -1, endIndexExclusive: 0};
        }

        if (this.isCurrentTimeWithinRange(this.highlightedRange)) {
            return this.highlightedRange;
        }

        const nextRange = this.newHighlightedRangeFromStartIndex(this.highlightedRange.endIndexExclusive);

        if (this.isCurrentTimeWithinRange(nextRange)) {
            return nextRange;
        }

        const startIndex = CollectionUtils.binarySearchInRange(
            this.segment.words,
            this.timeMillis,
            word => word.startOffsetMillis,
            word => word.endOffsetMillis
        );

        if (startIndex >= 0) {
            return this.newHighlightedRangeFromStartIndex(startIndex);
        }

        return {startIndexInclusive: -1, endIndexExclusive: 0};
    }

    private isCurrentTimeWithinRange(range: HighlightedRange): boolean {
        const words = this.segment.words;
        const start = words[range.startIndexInclusive];
        const end = words[range.endIndexExclusive - 1];

        return start
            && end
            && this.timeMillis < end.endOffsetMillis
            && this.timeMillis >= start.startOffsetMillis;
    }

    private newHighlightedRangeFromStartIndex(startIndexInclusive: number): HighlightedRange {
        return {
            startIndexInclusive: startIndexInclusive,
            endIndexExclusive: Math.min(this.segment.words.length, startIndexInclusive + this.maxHighlightedWords)
        };
    }

    private static newSequenceOnlyWordModel(sequence: number) {
        return {
            content: '',
            sequence: sequence,
            startOffsetMillis: -1,
            endOffsetMillis: -1,
            speakerName: ''
        } as WordDto;
    }

}