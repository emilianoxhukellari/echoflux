package transcribe.domain.transcript.transcript_part_text.service;

import org.springframework.validation.annotation.Validated;
import transcribe.domain.transcript.transcript_part_text.data.TranscriptPartTextEntity;

@Validated
public interface TranscriptPartTextService {

    /**
     * <p>
     * Adds a new transcript part text for the given transcript part. The new text will be added as the latest version.
     * </p>
     */
    TranscriptPartTextEntity add(AddTranscriptPartTextCommand command);

}
