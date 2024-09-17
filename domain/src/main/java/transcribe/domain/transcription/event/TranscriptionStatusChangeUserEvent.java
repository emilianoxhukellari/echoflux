package transcribe.domain.transcription.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.domain.application_user.event.ApplicationUserEvent;
import transcribe.domain.transcription.data.TranscriptionStatus;

/**
 * Emitted when the status of a transcription has changed. Statuses are {@link TranscriptionStatus#values()}.
 * */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptionStatusChangeUserEvent implements TranscriptionEvent, ApplicationUserEvent {

    @NotNull
    private Long applicationUserId;

    @NotNull
    private Long transcriptionId;

    @NotNull
    private TranscriptionStatus status;

}
