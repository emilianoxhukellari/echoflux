package transcribe.core.core.no_op;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public final class NoOp {

    public static Runnable runnable() {
        return () -> {};
    }

    public static <T> Consumer<T> consumer() {
        return _ -> {};
    }

}
