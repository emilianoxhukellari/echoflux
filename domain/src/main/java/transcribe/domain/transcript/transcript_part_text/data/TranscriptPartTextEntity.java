package transcribe.domain.transcript.transcript_part_text.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "transcript_part_text")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptPartTextEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "transcript_part_id")
    private Long transcriptPartId;

    @NotNull
    @Column(name = "content")
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
