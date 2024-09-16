package transcribe.core.core.progress;

@FunctionalInterface
public interface ProgressCallback {

    void onProgress(int progress);

}
