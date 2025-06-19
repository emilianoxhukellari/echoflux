package echoflux.core.core.tuple;

import lombok.Builder;

@Builder
public record Tuple3<T1, T2, T3>(T1 t1, T2 t2, T3 t3) {
}
