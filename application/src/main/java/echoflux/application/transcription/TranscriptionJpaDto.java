package echoflux.application.transcription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.PersistenceCreator;
import echoflux.annotation.core.ParentProperty;
import echoflux.annotation.jpa.JpaDto;
import echoflux.application.core.annotation.BigText;
import echoflux.application.user.ApplicationUserJpaDto;
import echoflux.core.core.annotation.Required;
import echoflux.core.transcribe.common.Language;
import echoflux.domain.transcription.data.TranscriptionEntity;
import echoflux.domain.transcription.data.TranscriptionStatus;

import java.net.URI;
import java.time.Duration;
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

    private Duration length;

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
