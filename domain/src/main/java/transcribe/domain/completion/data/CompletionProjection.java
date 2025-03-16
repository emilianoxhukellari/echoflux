package transcribe.domain.completion.data;

public record CompletionProjection(Long id,
                                   String input,
                                   String output,
                                   Integer inputTokens,
                                   Integer outputTokens,
                                   String model,
                                   Double temperature,
                                   Double topP,
                                   CompletionStatus status,
                                   Long durationMillis,
                                   String error) {
}
