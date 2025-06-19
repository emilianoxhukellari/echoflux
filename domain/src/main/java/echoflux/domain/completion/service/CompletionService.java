package echoflux.domain.completion.service;

import echoflux.domain.completion.data.ScalarCompletionProjection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CompletionService {

    ScalarCompletionProjection create(@Valid @NotNull CreateCompletionCommand command);

    ScalarCompletionProjection patch(@Valid @NotNull PatchCompletionCommand command);

}
