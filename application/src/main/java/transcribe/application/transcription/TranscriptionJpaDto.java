package transcribe.application.transcription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.application.core.annotation.BigText;
import transcribe.core.core.annotation.Required;
import transcribe.annotation.jpa.JpaDto;
import transcribe.core.transcribe.common.Language;
import transcribe.domain.transcription.data.TranscriptionEntity;
import transcribe.domain.transcription.data.TranscriptionStatus;

import java.net.URI;
import java.time.LocalDateTime;

@JpaDto(entityBeanType = TranscriptionEntity.class)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptionJpaDto {

    private Long id;

    @Required
    private TranscriptionStatus status;

    private URI sourceUri;

    private URI cloudUri;

    @Required
    private Language language;

    @Required
    private String name;

    @Required
    private Long applicationUserId;

    @Required
    private Boolean enhanced;

    private Long lengthMillis;

    private Long downloadDurationMillis;

    private Long processDurationMillis;

    private Long transcribeDurationMillis;

    @Required
    @BigText
    private String error;

    private Integer version;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

}
