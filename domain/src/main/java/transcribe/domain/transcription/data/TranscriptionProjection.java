package transcribe.domain.transcription.data;

import lombok.Builder;
import transcribe.core.transcribe.common.Language;

import java.net.URI;

@Builder
public record TranscriptionProjection(Long id,
                                      TranscriptionStatus status,
                                      URI sourceUri,
                                      URI cloudUri,
                                      Language language,
                                      String name,
                                      Boolean enhanced,
                                      Long lengthMillis,
                                      String error) {
}