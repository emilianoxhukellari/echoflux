package transcribe.core.document.exporter;

import transcribe.core.document.DocumentType;

import java.nio.file.Path;

public interface DocumentExporter {

    Path export(String text, DocumentType type);

}
