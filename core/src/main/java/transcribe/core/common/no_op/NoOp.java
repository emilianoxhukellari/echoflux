package transcribe.core.common.no_op;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public final class NoOp {

    public static Runnable runnable() {
        return () -> log.debug("No-op runnable");
    }

    public static <T> Consumer<T> consumer() {
        return _ -> log.debug("No-op consumer");
    }

}
