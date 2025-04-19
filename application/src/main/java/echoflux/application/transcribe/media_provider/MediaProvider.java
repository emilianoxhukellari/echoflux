package echoflux.application.transcribe.media_provider;

import java.util.function.Consumer;

public interface MediaProvider {

    void onReady(Consumer<MediaValue> onReady);

    void onClientCleared(Runnable onClientCleared);

    void clearAndCleanup();

}
