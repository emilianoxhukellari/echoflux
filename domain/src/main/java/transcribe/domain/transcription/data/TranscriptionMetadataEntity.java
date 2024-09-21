package transcribe.domain.transcription.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import transcribe.domain.audit.data.AuditEntity;

@Entity
@Table(name = "transcription_metadata")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TranscriptionMetadataEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "transcribe_progress")
    @Min(0)
    @Max(100)
    private Integer transcribeProgress;

    @Column(name = "length_millis")
    @Min(0)
    private Long lengthMillis;

    @Column(name = "download_duration_millis")
    @Min(0)
    private Long downloadDurationMillis;

    @Column(name = "process_duration_millis")
    @Min(0)
    private Long processDurationMillis;

    @Column(name = "transcribe_duration_millis")
    @Min(0)
    private Long transcribeDurationMillis;

}
