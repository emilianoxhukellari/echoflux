package transcribe.domain.transcript.transcript_part_text.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import transcribe.domain.audit.data.AuditEntity;
import transcribe.core.core.annotation.BigText;

@Entity
@Table(name = "transcript_part_text")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TranscriptPartTextEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "transcript_part_id")
    private Long transcriptPartId;

    @NotNull
    @Column(name = "content")
    @BigText
    private String content;

    @NotNull
    @Min(0)
    @Column(name = "version")
    private Integer version;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TranscriptPartTextType type;

}
