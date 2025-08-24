package echoflux.core.core.utils;

import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.nio.file.Path;

public final class MoreUris {

    @Nullable
    public static URI toUri(@Nullable String uri) {
        if (uri == null) {
            return null;
        }

        try {
            return URI.create(StringUtils.stripToEmpty(uri));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The provided URI is invalid");
        } catch (Throwable e) {
            throw new IllegalArgumentException("An error occurred while creating the URI");
        }
    }

    @Nullable
    public static URI toUri(@Nullable Path path) {
        if (path == null) {
            return null;
        }

        return path.toUri();
    }

}
