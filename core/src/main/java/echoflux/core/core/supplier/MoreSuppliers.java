package echoflux.core.core.supplier;

import java.util.function.Supplier;

public final class MoreSuppliers {

    public static Supplier<Void> ofRunnable(Runnable runnable) {
        return () -> {
            runnable.run();
            return null;
        };
    }

}
