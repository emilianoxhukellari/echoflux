package echoflux.domain.completion.data;

import echoflux.domain.core.data.BaseProjection;
import jakarta.annotation.Nullable;
import org.immutables.value.Value;

import java.time.Duration;

@Value.Immutable
public interface ScalarCompletionProjection extends BaseProjection<Long> {

    String getInput();

    @Nullable
    String getOutput();

    @Nullable
    Integer getInputTokens();

    @Nullable
    Integer getOutputTokens();

    @Nullable
    String getModel();

    @Nullable
    Double getTemperature();

    @Nullable
    Double getTopP();

    CompletionStatus getStatus();

    @Nullable
    Duration getDuration();

    @Nullable
    String getError();

}
