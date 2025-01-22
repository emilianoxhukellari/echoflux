package transcribe.application.transcription_word;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.application.core.annotation.BigText;
import transcribe.core.core.annotation.Required;
import transcribe.annotation.jpa.JpaDto;
import transcribe.application.transcription_speaker.TranscriptionSpeakerJpaDto;
import transcribe.annotation.core.ParentProperty;
import transcribe.domain.transcription_word.data.TranscriptionWordEntity;

import java.time.LocalDateTime;

@JpaDto(entityBeanType = TranscriptionWordEntity.class)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptionWordJpaDto {

    private Long id;

    @Required
    private Long transcriptionId;

    @Required
    private Integer sequence;

    @Required
    private Long startOffsetMillis;

    @Required
    private Long endOffsetMillis;

    @Required
    @BigText
    private String content;

    @Required
    @ParentProperty
    private TranscriptionSpeakerJpaDto transcriptionSpeaker;

    private Integer version;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

}
