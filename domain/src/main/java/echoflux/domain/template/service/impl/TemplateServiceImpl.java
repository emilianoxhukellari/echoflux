package echoflux.domain.template.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import echoflux.domain.template.data.TemplateEntity;
import echoflux.domain.template.data.TemplateRepository;
import echoflux.domain.template.service.RenderTemplateCommand;
import echoflux.domain.template.service.TemplateService;
import echoflux.template.renderer.RenderTemplateFromStringCommand;
import echoflux.template.renderer.TemplateRenderer;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository repository;
    private final TemplateRenderer renderer;

    @Override
    public TemplateEntity getByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new NoSuchElementException("Template not found"));
    }

    @Override
    public String render(RenderTemplateCommand command) {
        var template = getByName(command.getName());

        return renderer.render(
                RenderTemplateFromStringCommand.builder()
                        .template(template.getContent())
                        .dataModel(command.getDataModel())
                        .build()
        );
    }

}
