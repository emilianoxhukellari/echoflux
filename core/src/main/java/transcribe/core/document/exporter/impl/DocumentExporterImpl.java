package transcribe.core.document.exporter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import transcribe.core.core.bean.loader.BeanLoader;
import transcribe.core.document.DocumentType;
import transcribe.core.document.exporter.DocumentExporter;
import transcribe.core.document.spi.DocumentExporterSpi;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class DocumentExporterImpl implements DocumentExporter {

    private final BeanLoader beanLoader;

    @Override
    public Path export(String text, DocumentType type) {
        var exporter = beanLoader.loadWhen(
                DocumentExporterSpi.class,
                spi -> spi.supports(type)
        );

        return exporter.export(text);
    }

}
