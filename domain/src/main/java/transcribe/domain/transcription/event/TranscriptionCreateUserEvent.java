package transcribe.domain.transcription.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.domain.application_user.event.ApplicationUserEvent;
import transcribe.domain.application_user.event.TranscriptionEvent;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptionCreateUserEvent implements TranscriptionEvent, ApplicationUserEvent {

    @NotNull
    private Long applicationUserId;

    @NotNull
    private Long transcriptionId;

}
