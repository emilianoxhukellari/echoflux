package transcribe.core.document;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import transcribe.core.core.display_name.DisplayName;

@Getter
@RequiredArgsConstructor
public enum DocumentType {

    @DisplayName("Microsoft SpeechToTextWord (.docx)")
    DOCX("docx"),

    @DisplayName("PDF Document (.pdf)")
    PDF("pdf"),

    @DisplayName("Plain Text (.txt)")
    TXT("txt");

    private final String container;

}
