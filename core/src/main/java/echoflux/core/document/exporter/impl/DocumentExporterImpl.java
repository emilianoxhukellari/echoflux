package echoflux.core.document.exporter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import echoflux.core.core.bean.accessor.BeanAccessor;
import echoflux.core.document.DocumentType;
import echoflux.core.document.Paragraph;
import echoflux.core.document.exporter.DocumentExporter;
import echoflux.core.document.spi.DocumentExporterSpi;

import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentExporterImpl implements DocumentExporter {

    private final BeanAccessor beanAccessor;

    @Override
    public Path export(List<Paragraph> paragraphs, DocumentType type) {
        var exporter = beanAccessor.getWhen(
                DocumentExporterSpi.class,
                spi -> spi.supports(type)
        );

        return exporter.export(paragraphs);
    }
}
