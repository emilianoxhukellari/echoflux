package echoflux.core.storage;

import lombok.Builder;

@Builder
public record SaveOptions(String contentType, boolean temp) {
}
