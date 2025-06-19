package echoflux.template.renderer.impl;

import freemarker.template.Configuration;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.exec.util.MapUtils;
import org.springframework.stereotype.Component;
import echoflux.template.renderer.TemplateRenderer;

import java.io.StringWriter;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TemplateRendererImpl implements TemplateRenderer {

    private final static String DYNAMIC_TEMPLATE_NAME = "dynamic.ftl";
    private final static String DYNAMIC_TEMPLATE_CONTENT_KEY = "dynamicTemplateContent";

    private final Configuration configuration;

    @SneakyThrows
    public String renderFromFile(String templateName, Map<String, Object> dataModel) {
        var template = configuration.getTemplate(templateName);

        try(var out = new StringWriter()) {
            template.process(dataModel, out);

            return out.toString();
        }
    }

    @Override
    public String renderFromString(@NotBlank String template, Map<String, Object> dataModel) {
        var map = MapUtils.merge(
                Map.of(DYNAMIC_TEMPLATE_CONTENT_KEY, template),
                dataModel
        );

        return renderFromFile(DYNAMIC_TEMPLATE_NAME, map);
    }

}
