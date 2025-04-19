package echoflux.domain.completion.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import echoflux.domain.completion.data.CompletionEntity;
import echoflux.domain.completion.data.CompletionProjection;

@Validated
public interface CompletionService {

    CompletionEntity getById(@NotNull Long completionId);

    CompletionProjection create(@Valid @NotNull CreateCompletionCommand command);

    CompletionProjection patch(@Valid @NotNull PatchCompletionCommand command);

}
