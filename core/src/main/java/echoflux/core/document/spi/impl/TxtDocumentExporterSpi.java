package echoflux.core.document.spi.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import echoflux.core.core.temp_file.TempFileNameGenerator;
import echoflux.core.core.utils.MoreStrings;
import echoflux.core.document.DocumentType;
import echoflux.core.document.Paragraph;
import echoflux.core.document.spi.DocumentExporterSpi;
import echoflux.core.document.spi.DocumentTempDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class TxtDocumentExporterSpi implements DocumentExporterSpi, TempFileNameGenerator {

    @SneakyThrows(IOException.class)
    @Override
    public Path export(List<Paragraph> paragraphs) {
        var tempPath = DocumentTempDirectory.INSTANCE
                .locationPath()
                .resolve("%s.%s".formatted(newFileName(), DocumentType.TXT.getContainer()));

        var text = paragraphs.stream()
                .map(Paragraph::getContent)
                .collect(Collectors.joining(MoreStrings.LINE_SEPARATOR));

        Files.write(tempPath, text.getBytes());

        return tempPath;
    }

    @Override
    public boolean supports(DocumentType type) {
        return DocumentType.TXT.equals(type);
    }

    @Override
    public String fileNamePrefix() {
        return "txt";
    }

}
