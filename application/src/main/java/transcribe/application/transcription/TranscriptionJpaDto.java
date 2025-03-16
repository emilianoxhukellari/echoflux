package transcribe.application.transcription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.PersistenceCreator;
import transcribe.annotation.core.ParentProperty;
import transcribe.annotation.jpa.JpaDto;
import transcribe.application.core.annotation.BigText;
import transcribe.application.user.ApplicationUserJpaDto;
import transcribe.core.core.annotation.Required;
import transcribe.core.transcribe.common.Language;
import transcribe.domain.transcription.data.TranscriptionEntity;
import transcribe.domain.transcription.data.TranscriptionStatus;

import java.net.URI;
import java.time.LocalDateTime;

@JpaDto(entityBeanType = TranscriptionEntity.class)
@Data
@Builder
@AllArgsConstructor(onConstructor_ = @PersistenceCreator)
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
    private Boolean enhanced;

    private Long lengthMillis;

    @BigText
    private String error;

    @Required
    @ParentProperty
    private ApplicationUserJpaDto applicationUser;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

}
