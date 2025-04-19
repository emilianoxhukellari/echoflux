package echoflux.core.document.exporter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import echoflux.core.core.bean.loader.BeanLoader;
import echoflux.core.document.DocumentType;
import echoflux.core.document.Paragraph;
import echoflux.core.document.exporter.DocumentExporter;
import echoflux.core.document.spi.DocumentExporterSpi;

import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentExporterImpl implements DocumentExporter {

    private final BeanLoader beanLoader;

    @Override
    public Path export(List<Paragraph> paragraphs, DocumentType type) {
        var exporter = beanLoader.loadWhen(
                DocumentExporterSpi.class,
                spi -> spi.supports(type)
        );

        return exporter.export(paragraphs);
    }
}
