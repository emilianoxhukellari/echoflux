package transcribe.core.document.spi.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import transcribe.core.core.temp_file.TempFileNameGenerator;
import transcribe.core.document.DocumentType;
import transcribe.core.document.spi.DocumentExporterSpi;
import transcribe.core.document.spi.DocumentTempDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
@Slf4j
public class TxtDocumentExporterSpi implements DocumentExporterSpi, TempFileNameGenerator {

    @Override
    @SneakyThrows(IOException.class)
    public Path export(String text) {
        var tempPath = DocumentTempDirectory.INSTANCE
                .locationPath()
                .resolve("%s.%s".formatted(newFileName(), DocumentType.TXT.getContainer()));

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
