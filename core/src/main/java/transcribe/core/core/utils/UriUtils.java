package transcribe.core.core.utils;

import jakarta.annotation.Nullable;

import java.net.URI;
import java.nio.file.Path;

public final class UriUtils {

    public static @Nullable URI newUri(@Nullable String uri) {
        if (uri == null) {
            return null;
        }

        try {
            return URI.create(uri);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The provided URI is invalid");
        } catch (Throwable e) {
            throw new IllegalArgumentException("An error occurred while creating the URI");
        }
    }

    public static @Nullable URI toUri(@Nullable Path path) {
        if (path == null) {
            return null;
        }

        return path.toUri();
    }

}
