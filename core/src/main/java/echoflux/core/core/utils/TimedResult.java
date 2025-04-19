package echoflux.core.core.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimedResult<T> {

    private T result;

    @Builder.Default
    private Duration duration = Duration.ZERO;

}
