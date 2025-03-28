package transcribe.core.document.exporter;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import transcribe.core.document.DocumentType;
import transcribe.core.document.Paragraph;

import java.nio.file.Path;
import java.util.List;

@Validated
public interface DocumentExporter {

    Path export(@NotNull List<@NotNull Paragraph> paragraphs, @NotNull DocumentType type);

}
