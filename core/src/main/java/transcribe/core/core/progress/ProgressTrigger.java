package transcribe.core.core.progress;

import transcribe.core.function.FunctionUtils;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ProgressTrigger {

    private static final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final long durationNanos;
    private final ProgressCallback progressCallback;
    private final long tickIntervalMillis;
    private final long tickUntilProgress;

    public ProgressTrigger(long durationMillis,
                           long tickIntervalMillis,
                           long tickUntilProgress,
                           ProgressCallback progressCallback) {
        this.durationNanos = durationMillis * 1_000_000;
        this.tickIntervalMillis = tickIntervalMillis;
        this.tickUntilProgress = tickUntilProgress;
        this.progressCallback = Objects.requireNonNull(progressCallback, "Progress callback is required");
    }

    @SuppressWarnings("BusyWait")
    public void start() {
        if (running.compareAndSet(false, true)) {
            var startTime = System.nanoTime();
            var progress = new AtomicInteger();

            executor.execute(() -> {
                while (durationNanos > System.nanoTime() - startTime && progress.get() <= tickUntilProgress) {
                    try {
                        Thread.sleep(tickIntervalMillis);
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
                    } catch (InterruptedException _) {
                    }
                }
            });
        }
    }

    public void stop() {
        if (running.compareAndSet(true, false)) {
            FunctionUtils.runSynchronized(() -> progressCallback.onProgress(100), lock);
        }
    }

}
