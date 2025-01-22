package transcribe.core.document.spi;

import transcribe.core.document.DocumentType;

import java.nio.file.Path;

public interface DocumentExporterSpi {

    Path export(String text);

    boolean supports(DocumentType type);

}