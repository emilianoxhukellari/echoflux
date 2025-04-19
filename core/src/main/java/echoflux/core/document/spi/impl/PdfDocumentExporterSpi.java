package echoflux.core.document.spi.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import echoflux.core.core.temp_file.TempFileNameGenerator;
import echoflux.core.document.DocumentType;
import echoflux.core.document.Paragraph;
import echoflux.core.document.spi.DocumentExporterSpi;
import echoflux.core.document.spi.DocumentTempDirectory;
import echoflux.core.settings.Settings;
import echoflux.core.settings.SettingsLoader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PdfDocumentExporterSpi implements DocumentExporterSpi, TempFileNameGenerator {

    private final SettingsLoader settingsLoader;

    @SneakyThrows(DocumentException.class)
    @Override
    public Path export(List<Paragraph> paragraphs) {
        var tempPath = DocumentTempDirectory.INSTANCE
                .locationPath()
                .resolve("%s.%s".formatted(newFileName(), DocumentType.PDF.getContainer()));

        var settings = settingsLoader.load(PdfDocumentExporterSpiSettings.class);

        var font = new Font();
        font.setStyle(Font.NORMAL);
        font.setFamily(settings.getFontFamily());
        font.setSize(settings.getFontSize());

        var document = new Document(PageSize.A4);

        try (var out = Files.newOutputStream(tempPath)) {
            PdfWriter.getInstance(document, out);

            document.open();
            for (var p : paragraphs) {
                var paragraph = new com.itextpdf.text.Paragraph(p.getContent(), font);
                document.add(paragraph);
            }
            document.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return tempPath;
    }

    @Override
    public boolean supports(DocumentType type) {
        return DocumentType.PDF.equals(type);
    }

    @Override
    public String fileNamePrefix() {
        return "pdf";
    }

    @Settings(key = "4305eb31-9090-41ba-86b0-ed55266dc334")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PdfDocumentExporterSpiSettings {

        @Builder.Default
        private Integer fontSize = 12;

        @Builder.Default
        private String fontFamily = "Times-Roman";

    }

}
