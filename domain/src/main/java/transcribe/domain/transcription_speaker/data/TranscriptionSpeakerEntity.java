package transcribe.domain.transcription_speaker.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import transcribe.domain.audit.data.BaseEntity;
import transcribe.domain.transcription_word.data.TranscriptionWordEntity;

import java.util.List;

@Entity
@Table(name = "transcription_speaker")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SequenceGenerator(name = "transcription_speaker_id_seq", sequenceName = "transcription_speaker_id_seq", allocationSize = 1)
public class TranscriptionSpeakerEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transcription_speaker_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @NotBlank
    private String name;

    @JoinColumn(name = "transcription_id")
    @NotNull
    private Long transcriptionId;

    @OneToMany(mappedBy = "transcriptionSpeaker", fetch = FetchType.LAZY)
    private List<TranscriptionWordEntity> transcriptionWords;

}
