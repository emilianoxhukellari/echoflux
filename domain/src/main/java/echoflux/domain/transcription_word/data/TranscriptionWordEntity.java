package echoflux.domain.transcription_word.data;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import echoflux.core.word.common.WordInfo;
import echoflux.domain.audit.data.BaseEntity;
import echoflux.domain.transcription.data.TranscriptionEntity;

@Entity
@Table(name = "transcription_word")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SequenceGenerator(name = "transcription_word_id_seq", sequenceName = "transcription_word_id_seq", allocationSize = 30)
public class TranscriptionWordEntity extends BaseEntity implements WordInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transcription_word_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "content")
    @NotNull
    private String content;

    @Column(name = "speaker_name")
    @NotBlank
    private String speakerName;

    @Column(name = "start_offset_millis")
    @NotNull
    private Long startOffsetMillis;

    @Column(name = "end_offset_millis")
    @NotNull
    private Long endOffsetMillis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transcription_id")
    @NotNull
    private TranscriptionEntity transcription;

    /**
     * Do not update this field directly - {@link TranscriptionEntity} uses {@link JoinColumn} to order the words.
     * */
    @Column(name = "sequence")
    private Integer sequence;

}
