package echoflux.core.document.spi.impl;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
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
public class DocxDocumentExporterSpi implements DocumentExporterSpi, TempFileNameGenerator {

    private final SettingsLoader settingsLoader;

    @Override
    public Path export(List<Paragraph> paragraphs) {
        var tempPath = DocumentTempDirectory.INSTANCE
                .locationPath()
                .resolve("%s.%s".formatted(newFileName(), DocumentType.DOCX.getContainer()));

        var settings = settingsLoader.load(DocxDocumentExporterSpiSettings.class);

        try (var document = new XWPFDocument(); var out = Files.newOutputStream(tempPath)) {
            for (var p : paragraphs) {
                var paragraph = document.createParagraph();
                var run = paragraph.createRun();
                run.setFontFamily(settings.getFontFamily());
                run.setFontSize(settings.getFontSize());

                var parts = StringUtils.splitPreserveAllTokens(p.getContent(), StringUtils.LF);

                for (int i = 0; i < parts.length; i++) {
                    run.setText(parts[i]);
                    if (i < parts.length - 1) {
                        run.addCarriageReturn();
                    }
                }
            }

            document.write(out);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return tempPath;
    }

    @Override
    public boolean supports(DocumentType type) {
        return DocumentType.DOCX.equals(type);
    }

    @Override
    public String fileNamePrefix() {
        return "docx";
    }

    @Settings(key = "a1537dc0-d1e1-4b78-aa5e-72ed748f0c15")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DocxDocumentExporterSpiSettings {

        @Builder.Default
        @NotNull
        private Integer fontSize = 12;

        @Builder.Default
        @NotBlank
        private String fontFamily = "Arial";

    }

}
