package transcribe.core.core.progress;

import org.apache.commons.lang3.ThreadUtils;
import transcribe.core.core.executor.MoreExecutors;
import transcribe.core.core.utils.TsFunctions;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ProgressTrigger {

    private static final Executor executor = MoreExecutors.delegatingSecurityVirtualThreadExecutor();

    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final long durationNanos;
    private final ProgressCallback progressCallback;
    private final Duration tickInterval;
    private final long tickUntilProgress;

    public ProgressTrigger(long durationMillis,
                           long tickIntervalMillis,
                           long tickUntilProgress,
                           ProgressCallback progressCallback) {
        this.durationNanos = durationMillis * 1_000_000;
        this.tickInterval = Duration.ofMillis(tickIntervalMillis);
        this.tickUntilProgress = tickUntilProgress;
        this.progressCallback = Objects.requireNonNull(progressCallback, "Progress callback is required");
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            var startTime = System.nanoTime();
            var progress = new AtomicInteger();

            executor.execute(() -> {
                while (durationNanos > System.nanoTime() - startTime && progress.get() <= tickUntilProgress) {
                    ThreadUtils.sleepQuietly(tickInterval);

                    var p = (int) Math.min(tickUntilProgress, (System.nanoTime() - startTime) * 100 / durationNanos);
                    if (lock.tryLock()) {
                        try {
                            if (running.get()) {
                                progress.set(p);
                                progressCallback.onProgress(progress.get());
                            } else {
                                break;
                            }
                        } finally {
                            lock.unlock();
                        }
                    }
                }
            });
        }
    }

    public void stop() {
        if (running.compareAndSet(true, false)) {
            TsFunctions.runSynchronized(() -> progressCallback.onProgress(100), lock);
        }
    }

}
