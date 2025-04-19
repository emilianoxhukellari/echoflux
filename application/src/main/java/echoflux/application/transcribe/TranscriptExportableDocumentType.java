package echoflux.application.transcribe;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import echoflux.core.document.DocumentType;

@Getter
@RequiredArgsConstructor
public enum TranscriptExportableDocumentType {

    DOCX(DocumentType.DOCX),
    PDF(DocumentType.PDF),
    TXT(DocumentType.TXT);

    private final DocumentType documentType;

}
