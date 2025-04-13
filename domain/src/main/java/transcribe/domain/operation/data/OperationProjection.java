package transcribe.domain.operation.data;

import java.time.Duration;
import java.time.LocalDateTime;

public record OperationProjection(Long id,
                                  String name,
                                  OperationType type,
                                  OperationStatus status,
                                  String error,
                                  LocalDateTime startedAt,
                                  LocalDateTime endedAt,
                                  Duration duration,
                                  String description) {
}
