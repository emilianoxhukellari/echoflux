package echoflux.domain.transcription.data;

import echoflux.core.storage.StorageProvider;
import echoflux.core.transcribe.Language;
import echoflux.domain.core.data.BaseProjection;
import jakarta.annotation.Nullable;
import org.immutables.value.Value;

import java.net.URI;
import java.time.Duration;

@Value.Immutable
public interface ScalarTranscriptionProjection extends BaseProjection<Long> {

    TranscriptionStatus getStatus();

    @Nullable
    URI getSourceUri();

    @Nullable
    URI getUri();

    @Nullable
    StorageProvider getStorageProvider();

    Language getLanguage();

    String getName();

    @Nullable
    Duration getLength();

    @Nullable
    String getError();

}
