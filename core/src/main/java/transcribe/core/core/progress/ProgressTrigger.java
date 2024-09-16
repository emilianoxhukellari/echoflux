package transcribe.core.core.progress;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ProgressTrigger {

    private static final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private static final int TICK_INTERVAL_MILLIS = 1000;

    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Duration duration;
    private final ProgressCallback progressCallback;
    private final int tickUntilProgress;

    public ProgressTrigger(Duration duration, ProgressCallback progressCallback, int tickUntilProgress) {
        this.duration = Objects.requireNonNull(duration, "Duration is required");
        this.progressCallback = Objects.requireNonNull(progressCallback, "Progress callback is required");
        this.tickUntilProgress = tickUntilProgress;
    }

    @SuppressWarnings("BusyWait")
    public void start() {
        if (running.compareAndSet(false, true)) {
            var startTime = System.nanoTime();
            var progress = new AtomicInteger();

            executor.execute(() -> {
                while (duration.toNanos() > System.nanoTime() - startTime && progress.get() <= tickUntilProgress) {
                    try {
                        Thread.sleep(TICK_INTERVAL_MILLIS);
                        var p = (int) Math.min(tickUntilProgress, (System.nanoTime() - startTime) * 100 / duration.toNanos());
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
            lock.lock();
            try {
                progressCallback.onProgress(100);
            } finally {
                lock.unlock();
            }
        }
    }

}
