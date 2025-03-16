import {css, html, LitElement, PropertyValues} from 'lit';
import {customElement, property, state} from 'lit/decorators.js';
import {classMap} from 'lit/directives/class-map.js';
import '@vaadin/icon';
import '@vaadin/icons';
import '@vaadin/popover';
import {SpeakerSegmentDto} from "Frontend/element/audio-text-connector/SpeakerSegmentDto";
import {WordDto} from "Frontend/element/audio-text-connector/WordDto";
import {CollectionUtils} from "Frontend/utils/CollectionUtils";

@customElement('speaker-segment')
export class SpeakerSegment extends LitElement {

    @property({type: Object})
    segment!: SpeakerSegmentDto;

    @property({type: Number})
    currentTimeMillis = 0;

    @property({type: Number})
    startAtWordSequence = -1;

    @property({type: Number})
    maxHighlightedWords = 15;

    @state({
        hasChanged(value: HighlightedRange, oldValue: HighlightedRange): boolean {
            return !oldValue
                || value.startIndexInclusive !== oldValue.startIndexInclusive
                || value.endIndexExclusive !== oldValue.endIndexExclusive;
        }
    })
    private highlightedRange: HighlightedRange = {startIndexInclusive: -1, endIndexExclusive: 0};

    static readonly styles = css`
        .segment {
            display: flex;
        }

        .speaker {
            margin-right: 10px;
            text-align: right;
            padding: 3px 0;
            user-select: none;
        }

        .words {
            display: flex;
            flex-wrap: wrap;
            gap: 4px;
        }

        .word {
            cursor: pointer;
            display: inline;
            padding: 3px 0;
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
    `;

    protected render() {
        return html`
            <div class="segment">
                <div class="speaker">${this.segment.speakerName}:</div>
                <div class="words}">
                    ${this.segment.words.map((word: WordDto, index: number) => {
                        const wordClassInfo = {
                            highlighted: index >= this.highlightedRange.startIndexInclusive
                                    && index < this.highlightedRange.endIndexExclusive
                        };
                        return html`
                            <span id="word-${word.sequence}"
                                  class="word ${classMap(wordClassInfo)}"
                                  @click="${() => this.onWordClick(word)}">${word.content}</span>
                        `;
                    })}
                </div>
            </div>
        `;
    }

    private onWordClick(word: WordDto) {
        this.dispatchEvent(new CustomEvent('word-click', {detail: word, bubbles: true}));
    }

    protected willUpdate(_changedProperties: PropertyValues) {
        if (_changedProperties.has('startAtWordSequence')
            && this.startAtWordSequence >= this.segment.words[0].sequence
            && this.startAtWordSequence <= this.segment.words[this.segment.words.length - 1].sequence) {

            const newStartIndex = CollectionUtils.binarySearch(
                this.segment.words,
                SpeakerSegment.newSequenceOnlyWordModel(this.startAtWordSequence),
                (a, b) => a.sequence - b.sequence
            );

            if (newStartIndex >= 0) {
                this.highlightedRange = this.newHighlightedRangeFromStartIndex(newStartIndex);
            }
        }

        if (_changedProperties.has('currentTimeMillis')) {
            this.highlightedRange = this.getHighlightedRange();
        }
    }

    private getHighlightedRange(): HighlightedRange {
        if (this.currentTimeMillis >= this.segment.endOffsetMillis
            || this.currentTimeMillis < this.segment.startOffsetMillis) {

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
            this.currentTimeMillis,
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
            && this.currentTimeMillis < end.endOffsetMillis
            && this.currentTimeMillis >= start.startOffsetMillis;
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