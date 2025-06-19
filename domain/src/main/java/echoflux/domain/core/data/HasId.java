package echoflux.domain.core.data;

import jakarta.annotation.Nullable;

public interface HasId<ID> {

    String ID = "id";

    @Nullable
    ID getId();

}
