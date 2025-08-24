package echoflux.domain.transcription.endpoint;

import echoflux.domain.core.security.PermissionType;
import echoflux.domain.core.security.RequiredPermissions;
import echoflux.domain.jooq.tables.pojos.Transcription;
import echoflux.domain.transcription.service.RenameTranscriptionCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface TranscriptionEndpoint {

    Transcription getTranscriptionById(@NotNull Long id);

    @RequiredPermissions(PermissionType.TRANSCRIPTION_UPDATE)
    Long renameTranscription(@Valid @NotNull RenameTranscriptionCommand command);

}
