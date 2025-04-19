package echoflux.core.document.spi;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import echoflux.core.document.DocumentType;
import echoflux.core.document.Paragraph;

import java.nio.file.Path;
import java.util.List;

@Validated
public interface DocumentExporterSpi {

    Path export(@NotNull List<@NotNull Paragraph> paragraphs);

    boolean supports(@NotNull DocumentType type);

}