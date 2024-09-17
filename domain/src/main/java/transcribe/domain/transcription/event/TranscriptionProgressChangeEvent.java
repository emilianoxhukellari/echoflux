package transcribe.domain.transcription.event;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import transcribe.domain.application_user.event.ApplicationUserEvent;
import transcribe.domain.transcription.data.TranscriptionStatus;

/**
 * Emitted when some measurable progress has been made in a process of the pipeline. Not all processes will emit this event.
 * {@link TranscriptionStatus#DOWNLOADING_PUBLIC} and {@link TranscriptionStatus#TRANSCRIBING} will emit this event.
 * */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptionProgressChangeEvent implements TranscriptionEvent, ApplicationUserEvent {

    @NotNull
    private Long applicationUserId;

    @NotNull
    private Long transcriptionId;

    @Min(0)
    @Max(100)
    private int progress;

}
