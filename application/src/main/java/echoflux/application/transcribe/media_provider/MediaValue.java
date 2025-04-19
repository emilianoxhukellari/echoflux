package echoflux.application.transcribe.media_provider;

import echoflux.domain.transcription.data.MediaOrigin;

import java.net.URI;

public record MediaValue(URI uri, String name, MediaOrigin mediaOrigin) {
}
