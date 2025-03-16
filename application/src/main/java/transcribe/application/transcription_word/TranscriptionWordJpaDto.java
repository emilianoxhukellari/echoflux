package transcribe.application.transcription_word;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceCreator;
import transcribe.annotation.core.ParentProperty;
import transcribe.annotation.jpa.JpaDto;
import transcribe.application.core.annotation.BigText;
import transcribe.application.transcription.TranscriptionJpaDto;
import transcribe.core.core.annotation.Required;
import transcribe.domain.transcription_word.data.TranscriptionWordEntity;

import java.time.LocalDateTime;

@JpaDto(entityBeanType = TranscriptionWordEntity.class)
@Data
@Builder
@AllArgsConstructor(onConstructor_ = @PersistenceCreator)
@NoArgsConstructor
public class TranscriptionWordJpaDto {

    private Long id;

    @Required
    @BigText
    private String content;

    @Required
    private String speakerName;

    @Required
    private Long startOffsetMillis;

    @Required
    private Long endOffsetMillis;

    @Setter(value = AccessLevel.NONE)
    private Integer sequence;

    @Required
    @ParentProperty
    private TranscriptionJpaDto transcription;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

}
