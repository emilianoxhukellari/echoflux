package transcribe.core.core.supplier;

import java.util.function.Supplier;

public final class MoreSuppliers {

    public static <T> Supplier<T> of(Supplier<T> supplier) {
        return supplier;
    }

}
