package echoflux.domain.template.endpoint.impl;

import echoflux.domain.core.security.Endpoint;
import echoflux.domain.template.endpoint.TemplateEndpoint;
import echoflux.domain.template.service.SaveTemplateCommand;
import echoflux.domain.template.service.TemplateService;
import lombok.RequiredArgsConstructor;

@Endpoint
@RequiredArgsConstructor
public class TemplateEndpointImpl implements TemplateEndpoint {

    private final TemplateService templateService;

    @Override
    public Long save(SaveTemplateCommand command) {
        return templateService.save(command);
    }

    @Override
    public void deleteById(Long id) {
        templateService.deleteById(id);
    }

}
