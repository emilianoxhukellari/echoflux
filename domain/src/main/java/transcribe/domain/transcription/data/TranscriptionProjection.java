package transcribe.domain.transcription.data;

import lombok.Builder;
import transcribe.core.transcribe.common.Language;

import java.net.URI;
import java.time.Duration;

@Builder
public record TranscriptionProjection(Long id,
                                      TranscriptionStatus status,
                                      URI sourceUri,
                                      URI cloudUri,
                                      Language language,
                                      String name,
                                      Boolean enhanced,
                                      Duration length,
                                      String error) {
}