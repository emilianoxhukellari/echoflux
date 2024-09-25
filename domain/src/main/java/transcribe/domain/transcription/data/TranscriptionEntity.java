package transcribe.domain.transcription.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import transcribe.core.transcribe.common.Language;
import transcribe.domain.audit.data.AuditEntity;

import java.net.URI;

@Entity
@Table(name = "transcription")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TranscriptionEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "status")
    @NotNull
    @Enumerated(EnumType.STRING)
    private TranscriptionStatus status;

    @Column(name = "cloud_uri")
    private URI cloudUri;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Language language;

    @Column(name = "name")
    @NotBlank
    private String name;

    @Column(name = "transcript")
    private String transcript;

    @Column(name = "application_user_id")
    @NotNull
    private Long applicationUserId;

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

    @Column(name = "error")
    private String error;

}
