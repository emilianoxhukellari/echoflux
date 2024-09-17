package transcribe.domain.transcription.event;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.domain.application_user.event.ApplicationUserEvent;

/**
 * Emitted when the length of the audio file of the transcription is known.
 * */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptionLengthEvent implements TranscriptionEvent, ApplicationUserEvent {

    @NotNull
    private Long applicationUserId;

    @NotNull
    private Long transcriptionId;

    @Min(0)
    private long lengthMillis;

}
