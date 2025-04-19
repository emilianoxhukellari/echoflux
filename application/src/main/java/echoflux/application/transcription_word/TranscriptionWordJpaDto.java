package echoflux.application.transcription_word;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceCreator;
import echoflux.annotation.core.ParentProperty;
import echoflux.annotation.jpa.JpaDto;
import echoflux.application.core.annotation.BigText;
import echoflux.application.transcription.TranscriptionJpaDto;
import echoflux.core.core.annotation.Required;
import echoflux.domain.transcription_word.data.TranscriptionWordEntity;

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
