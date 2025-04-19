package echoflux.template.renderer.impl;

import freemarker.template.Configuration;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.exec.util.MapUtils;
import org.springframework.stereotype.Component;
import echoflux.template.renderer.RenderTemplateFromFileCommand;
import echoflux.template.renderer.RenderTemplateFromStringCommand;
import echoflux.template.renderer.TemplateRenderer;

import java.io.StringWriter;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TemplateRendererImpl implements TemplateRenderer {

    private final static String DYNAMIC_TEMPLATE_NAME = "dynamic.ftl";
    private final static String DYNAMIC_TEMPLATE_CONTENT_KEY = "dynamicTemplateContent";

    private final Configuration configuration;

    @Override
    @SneakyThrows
    public String render(RenderTemplateFromFileCommand command) {
        var template = configuration.getTemplate(command.getTemplateName());

        @Cleanup
        var out = new StringWriter();
        template.process(command.getDataModel(), out);

        return out.toString();
    }

    @Override
    public String render(RenderTemplateFromStringCommand command) {
        var map = MapUtils.merge(
                Map.of(DYNAMIC_TEMPLATE_CONTENT_KEY, command.getTemplate()),
                command.getDataModel()
        );

        return render(
                RenderTemplateFromFileCommand.builder()
                        .templateName(DYNAMIC_TEMPLATE_NAME)
                        .dataModel(map)
                        .build()
        );
    }

}
