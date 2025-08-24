package echoflux.domain.completion.service;

import echoflux.domain.jooq.tables.pojos.Completion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CompletionService {

    Completion create(@Valid @NotNull CreateCompletionCommand command);

    Completion patch(@Valid @NotNull PatchCompletionCommand command);

}
