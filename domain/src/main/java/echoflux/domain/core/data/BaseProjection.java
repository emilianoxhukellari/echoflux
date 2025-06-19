package echoflux.domain.core.data;

import jakarta.annotation.Nullable;

import java.time.Instant;

public interface BaseProjection<ID> extends HasId<ID> {

    @Nullable
    Instant getCreatedAt();

    @Nullable
    String getCreatedBy();

    @Nullable
    Instant getUpdatedAt();

    @Nullable
    String getUpdatedBy();

}
