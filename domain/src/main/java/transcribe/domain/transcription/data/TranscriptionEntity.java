package transcribe.domain.transcription.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import transcribe.core.transcribe.common.Language;
import transcribe.domain.audit.data.AuditEntity;
import transcribe.core.core.annotation.BigText;

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

    @Column(name = "source_uri")
    private URI sourceUri;

    @Column(name = "cloud_uri")
    private URI cloudUri;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Language language;

    @Column(name = "name")
    @NotBlank
    private String name;

    @Column(name = "application_user_id")
    @NotNull
    private Long applicationUserId;

    @Column(name = "enhanced")
    @NotNull
    private Boolean enhanced;

    @Column(name = "completion_id")
    private Long completionId;

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
    @BigText
    private String error;

}
