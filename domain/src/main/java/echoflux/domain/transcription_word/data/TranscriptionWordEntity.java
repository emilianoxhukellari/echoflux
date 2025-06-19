package echoflux.domain.transcription_word.data;

import echoflux.core.word.common.HasSequence;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import echoflux.core.word.common.WordInfo;
import echoflux.domain.core.data.BaseEntity;
import echoflux.domain.transcription.data.TranscriptionEntity;

@Entity
@Table(name = "transcription_word")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = "id")
public class TranscriptionWordEntity extends BaseEntity<Long> implements WordInfo, HasSequence {

    @Id
    @Tsid
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
