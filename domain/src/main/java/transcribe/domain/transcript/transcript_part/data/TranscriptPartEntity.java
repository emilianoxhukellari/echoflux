package transcribe.domain.transcript.transcript_part.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JoinFormula;
import transcribe.domain.audit.data.AuditEntity;
import transcribe.core.core.annotation.ParentProperty;
import transcribe.domain.transcript.transcript_part_text.data.TranscriptPartTextEntity;

@Entity
@Table(name = "transcript_part")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TranscriptPartEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "transcription_id")
    private Long transcriptionId;

    @NotNull
    @Column(name = "start_offset_millis")
    private Long startOffsetMillis;

    @NotNull
    @Column(name = "end_offset_millis")
    private Long endOffsetMillis;

    @NotNull
    @Column(name = "sequence")
    @Min(0)
    private Integer sequence;

    @ManyToOne
    @JoinFormula("""
            (SELECT tpt.id
            FROM transcript_part_text tpt
            WHERE tpt.transcript_part_id = id
            ORDER BY tpt.version DESC
            LIMIT 1)
            """)
    @ParentProperty
    private TranscriptPartTextEntity latestTextEntity;

}
