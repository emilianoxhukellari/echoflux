package echoflux.domain.template.endpoint;

import echoflux.domain.core.security.PermissionType;
import echoflux.domain.core.security.RequiredPermissions;
import echoflux.domain.template.service.SaveTemplateCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface TemplateEndpoint {

    @RequiredPermissions({PermissionType.TEMPLATE_CREATE, PermissionType.TEMPLATE_UPDATE})
    Long save(@Valid @NotNull SaveTemplateCommand command);

    @RequiredPermissions(PermissionType.TEMPLATE_DELETE)
    void deleteById(@NotNull Long id);

}
