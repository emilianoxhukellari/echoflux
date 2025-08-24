package echoflux.domain.transcription.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import echoflux.domain.access_management.application_user.event.ApplicationUserEvent;
import echoflux.domain.access_management.application_user.event.TranscriptionEvent;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptionUpdateUserEvent implements TranscriptionEvent, ApplicationUserEvent {

    @NotNull
    private Long applicationUserId;

    @NotNull
    private Long transcriptionId;

}
