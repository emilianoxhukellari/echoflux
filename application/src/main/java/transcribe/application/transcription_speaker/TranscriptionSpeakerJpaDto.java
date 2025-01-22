package transcribe.application.transcription_speaker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.annotation.jpa.JpaDto;
import transcribe.core.core.annotation.Required;
import transcribe.domain.transcription_speaker.data.TranscriptionSpeakerEntity;

import java.time.LocalDateTime;

@JpaDto(entityBeanType = TranscriptionSpeakerEntity.class)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptionSpeakerJpaDto {

    private Long id;

    @Required
    private String name;

    @Required
    private Long transcriptionId;

    private Integer version;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

}
