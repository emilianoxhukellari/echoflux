package echoflux.domain.completion.data;

import java.time.Duration;

public record CompletionProjection(Long id,
                                   String input,
                                   String output,
                                   Integer inputTokens,
                                   Integer outputTokens,
                                   String model,
                                   Double temperature,
                                   Double topP,
                                   CompletionStatus status,
                                   Duration duration,
                                   String error) {
}
