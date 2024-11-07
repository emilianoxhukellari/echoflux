package transcribe.domain.template.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import transcribe.domain.template.data.TemplateEntity;
import transcribe.domain.template.data.TemplateRepository;
import transcribe.domain.template.service.RenderTemplateCommand;
import transcribe.domain.template.service.TemplateService;
import transcribe.template.renderer.RenderTemplateFromStringCommand;
import transcribe.template.renderer.TemplateRenderer;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository repository;
    private final TemplateRenderer renderer;

    @Override
    public TemplateEntity get(String name) {
        return repository.findByName(name)
                .orElseThrow();
    }

    @Override
    public String render(RenderTemplateCommand command) {
        var template = get(command.getName());

        return renderer.render(
                RenderTemplateFromStringCommand.builder()
                        .template(template.getContent())
                        .dataModel(command.getDataModel())
                        .build()
        );
    }

}
