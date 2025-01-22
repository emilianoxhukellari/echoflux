package transcribe.domain.completion.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.domain.completion.data.CompletionEntity;

@Validated
public interface CompletionService {

    CompletionEntity get(@NotNull Long completionId);

    CompletionEntity create(@Valid @NotNull CreateCompletionCommand command);

    CompletionEntity patch(@Valid @NotNull PatchCompletionCommand command);

}
