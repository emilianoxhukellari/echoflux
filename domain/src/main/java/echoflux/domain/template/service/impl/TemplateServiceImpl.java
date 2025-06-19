package echoflux.domain.template.service.impl;

import echoflux.domain.template.data.TemplateEntity;
import echoflux.domain.template.service.SaveTemplateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import echoflux.domain.template.data.TemplateRepository;
import echoflux.domain.template.service.RenderTemplateCommand;
import echoflux.domain.template.service.TemplateService;
import echoflux.template.renderer.TemplateRenderer;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;
    private final TemplateRenderer templateRenderer;

    @Override
    public String render(RenderTemplateCommand command) {
        var template = templateRepository.getProjectionByName(command.getName());

        return templateRenderer.renderFromString(template.getContent(), command.getDataModel());
    }

    @Transactional
    @Override
    public TemplateEntity save(SaveTemplateCommand command) {
        var template = command.getId() != null
                ? templateRepository.getReferenceById(command.getId())
                : new TemplateEntity();

        template.setName(command.getName());
        template.setContent(command.getContent());

        return templateRepository.save(template);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        templateRepository.deleteById(id);
    }

}
