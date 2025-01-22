package transcribe.domain.transcription_word.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import transcribe.domain.audit.data.BaseEntity;
import transcribe.domain.transcription_speaker.data.TranscriptionSpeakerEntity;

@Entity
@Table(name = "transcription_word")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SequenceGenerator(name = "transcription_word_id_seq", sequenceName = "transcription_word_id_seq", allocationSize = 30)
public class TranscriptionWordEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transcription_word_id_seq")
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "transcription_id")
    @NotNull
    private Long transcriptionId;

    @NotNull
    @Column(name = "sequence")
    @Min(0)
    private Integer sequence;

    @Column(name = "start_offset_millis")
    @NotNull
    private Long startOffsetMillis;

    @Column(name = "end_offset_millis")
    @NotNull
    private Long endOffsetMillis;

    @Column(name = "content")
    @NotNull
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transcription_speaker_id")
    @NotNull
    private TranscriptionSpeakerEntity transcriptionSpeaker;

}
