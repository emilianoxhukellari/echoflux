package echoflux.core.core.provider;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface AiProviderAware {

    @NotNull
    AiProvider getProvider();

}
